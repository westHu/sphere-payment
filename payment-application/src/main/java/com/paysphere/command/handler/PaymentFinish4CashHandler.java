package com.paysphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.cmd.PaymentFinishMessageCommand;
import com.paysphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import com.paysphere.command.dto.trade.result.MerchantResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.TradeCashSourceEnum;
import com.paysphere.enums.TradeModeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.settle.SettleCashMqMessageDTO;
import com.paysphere.mq.dto.settle.UnfrozenMessageDTO;
import com.paysphere.repository.TradePayoutOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component
public class PaymentFinish4CashHandler {

    @Resource
    TradePayoutOrderService tradePayoutOrderService;
    @Resource
    RedisService redisService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 代付支付结果, 更新状态、结果、时间
     */
    public void handlerPayment4FinishCash(PaymentFinishMessageCommand command) {
        log.info("handlerPayment4FinishCash command={}", JSONUtil.toJsonStr(command));
        String key = TradeConstant.LOCK_PREFIX_PAYMENT_LISTENER + command.getTradeNo();
        redisService.lock(key, () -> doPaymentFinish4Cash(command));
    }

    /**
     * 代付支付后操作
     */
    private boolean doPaymentFinish4Cash(PaymentFinishMessageCommand command) {
        String tradeNo = command.getTradeNo();

        // 传入的状态也必须是终态
        if (!PaymentStatusEnum.getFinalStatus().contains(command.getPaymentStatus())) {
            log.error("doPaymentFinish4Cash payment status must be final status. {}", command.getPaymentStatus());
            throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Not final status. refer to" + tradeNo);
        }
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(command.getPaymentStatus());
        log.info("handlerPayment4FinishCash paymentStatusEnum={}", paymentStatusEnum.name());

        // 处理订单
        TradePayoutOrder order = handlerCashOrder(command, paymentStatusEnum);

        // 结算-解冻/出款
        doSettleCashOrder(order, command, paymentStatusEnum);

        // API订单 执行商户回调
        TradeCashSourceEnum sourceEnum = TradeCashSourceEnum.codeToEnum(order.getSource());
        log.info("handlerPayment4FinishCash sourceEnum={}", sourceEnum.name());
        if (TradeCashSourceEnum.API.equals(sourceEnum)) {
            TradeCallBackDTO callBackDTO = buildCashOrderCallBackDTO(order, paymentStatusEnum, sourceEnum);
            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.TRADE_CALLBACK_TOPIC, JSONUtil.toJsonStr(callBackDTO));
            log.info("handlerPayment4FinishCash callback message: {}", sendResult);
            if (Objects.isNull(sendResult) || !sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                log.error("handlerPayment4FinishCash callback message sendResult error. {}", command.getTradeNo());
                throw new PaymentException(ExceptionCode.MESSAGE_MQ_ERROR, tradeNo);
            }
        }

        return true;
    }

    /**
     * 处理订单
     */
    private TradePayoutOrder handlerCashOrder(PaymentFinishMessageCommand command, PaymentStatusEnum statusEnum) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradePayoutOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderService.getOne(queryWrapper);
        if (Objects.isNull(order)) {
            log.error("doPaymentFinish4Cash payment cash order not exist, tradeNo={}", tradeNo);
            throw new PaymentException(ExceptionCode.CASH_ORDER_NOT_EXIST, tradeNo);
        }

        // 交易状态必须是成功 代付可能会先收到回调后接到同步的Response
        /*TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_SUCCESS.equals(tradeStatusEnum)) {
            log.error("doPaymentFinish4Cash tradeStatus not success, tradeStatusEnum={}", tradeStatusEnum);
            throw new PaymentException(ExceptionCode.MESSAGE_CONSUMER_LATER);
        }*/

        // 订单已经成功或者失败等终态
        if (PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus())) {
            log.error("doPaymentFinish4Cash cash order already final, paymentStatus={}", order.getPaymentStatus());
            throw new PaymentException(ExceptionCode.CASH_ORDER_FINAL_STATUS, tradeNo);
        }

        // 更新订单支付状态、结果、时间
//        order.setPaymentFinishTime(LocalDateTime.parse(command.getTransactionTime(), TradeConstant.DF_0));
        UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
        cashOrderUpdate.lambda()
                .set(TradePayoutOrder::getPaymentStatus, statusEnum.getCode())
                .set(TradePayoutOrder::getPaymentResult, JSONUtil.toJsonStr(command))
                .set(TradePayoutOrder::getPaymentFinishTime, order.getPaymentFinishTime())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePayoutOrder::getId, order.getId());
        boolean update = tradePayoutOrderService.update(cashOrderUpdate);
        log.info("handlerPayment4FinishCash update order result={}", update);
        return order;
    }

    /**
     * 代付结算 - 代付结算，组装数据，消息交互
     */
    private void doSettleCashOrder(TradePayoutOrder order, PaymentFinishMessageCommand command,
                                   PaymentStatusEnum statusEnum) {
        if (PaymentStatusEnum.PAYMENT_SUCCESS.equals(statusEnum)) {
            BigDecimal amount = order.getAmount();
            BigDecimal channelCost = order.getChannelCost();
            BigDecimal platformProfit = order.getPlatformProfit();
            BigDecimal merchantFee = order.getMerchantFee();
            BigDecimal merchantProfit = order.getMerchantProfit();

            // 如果此时同步还未返回或者查询时同步未更新完成-判断条件就是交易状态不是成功 -> 则自行计算通道成本和平台利润
            PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
            log.info("doSettleCashOrder tradeStatusEnum={}", paymentStatusEnum);
            if (!PaymentStatusEnum.PAYMENT_PROCESSING.equals(paymentStatusEnum)) {
                channelCost = command.getChannelCost();
                platformProfit = merchantFee.subtract(merchantProfit).subtract(channelCost);
                log.info("doSettleCashOrder resp not yet channelCost={} platformProfit={}", channelCost, platformProfit);
            }

            MerchantResultDTO merchantResult = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                    .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                    .map(TradeResultDTO::getMerchantResult)
                    .orElseThrow(() -> new PaymentException(ExceptionCode.PARAM_IS_REQUIRED, "merchantResult"));

//            LocalDateTime paymentFinishTime = Objects.nonNull(order.getPaymentFinishTime())
//                    ? order.getPaymentFinishTime()
//                    : LocalDateTime.now();

            SettleCashMqMessageDTO messageDTO = new SettleCashMqMessageDTO();
            messageDTO.setBusinessNo(order.getBusinessNo());
            messageDTO.setTradeNo(order.getTradeNo());
            messageDTO.setOuterNo(order.getOuterNo());
//            messageDTO.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
//            messageDTO.setPaymentFinishTime(paymentFinishTime.format(TradeConstant.DF_0));

            messageDTO.setCurrency(order.getCurrency());
            messageDTO.setAmount(amount);
            messageDTO.setActualAmount(order.getActualAmount());
            messageDTO.setMerchantProfit(merchantProfit);
            messageDTO.setMerchantFee(merchantFee);
            messageDTO.setAccountAmount(order.getAccountAmount());
            messageDTO.setChannelCost(channelCost);
            messageDTO.setPlatformProfit(platformProfit);

            messageDTO.setChannelCode(command.getChannelCode());
            messageDTO.setChannelName(command.getChannelName());
            messageDTO.setPaymentMethod(command.getPaymentMethod());
            messageDTO.setPaymentName(command.getPaymentName());
            messageDTO.setMerchantId(order.getMerchantId());
            messageDTO.setMerchantName(order.getMerchantName());
            messageDTO.setAccountNo(order.getAccountNo());
            messageDTO.setDeductionType(merchantResult.getDeductionType());

            log.info("handlerPayment4FinishCash settle messageDTO={}", JSONUtil.toJsonStr(messageDTO));
            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.SETTLE_CASH_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("handlerPayment4FinishCash settle message result={}", sendResult);
            if (Objects.isNull(sendResult) || !sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                log.error("handlerPayment4FinishCash settlement message sendResult error. {}", command.getTradeNo());
                throw new PaymentException(ExceptionCode.MESSAGE_MQ_ERROR);
            }
        } else {
            UnfrozenMessageDTO unfrozenMessageDTO = new UnfrozenMessageDTO();
            unfrozenMessageDTO.setTradeNo(order.getTradeNo());
            unfrozenMessageDTO.setOuterNo(order.getOuterNo());

            log.info("handlerPayment4FinishCash unfrozenMessageDTO={}", JSONUtil.toJsonStr(unfrozenMessageDTO));
            SendResult sendResult = rocketMqProducer.syncSend(TradeConstant.UNFROZEN_TOPIC, JSONUtil.toJsonStr(unfrozenMessageDTO));
            log.info("handlerPayment4FinishCash tradeNo={}, result={}", order.getTradeNo(), sendResult);
            if (Objects.isNull(sendResult) || !SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "cashUnfrozenAmount error. tradeNo : " + order.getTradeNo());
            }
        }
    }

    /**
     * 构建回调消息体
     */
    private TradeCallBackDTO buildCashOrderCallBackDTO(TradePayoutOrder cashOrder, PaymentStatusEnum statusEnum,
                                                       TradeCashSourceEnum sourceEnum) {
        Optional<MerchantResultDTO> merchantResultDTO = Optional.of(cashOrder).map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult);

//        ZonedDateTime tradeTime = Optional.of(cashOrder)
//                .map(TradePayoutOrder::getTradeTime)
//                .map(e -> e.atZone(TradeConstant.ZONE_ID))
//                .orElse(ZonedDateTime.now());
        // 解析回调地址
        String finishCashUrl = merchantResultDTO.map(MerchantResultDTO::getFinishCashUrl).orElse(null);
        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(cashOrder.getTradeNo());
        bodyDTO.setOrderNo(cashOrder.getOuterNo());
        bodyDTO.setMerchantId(cashOrder.getMerchantId());
        bodyDTO.setMerchantName(cashOrder.getMerchantName());
//        bodyDTO.setStatus(statusEnum.getMerchantStatus());
//        bodyDTO.setTransactionTime(tradeTime.format(TradeConstant.DF_3));

        TradeCallBackMoneyDTO money = new TradeCallBackMoneyDTO();
        money.setCurrency(cashOrder.getCurrency());
        money.setAmount(cashOrder.getActualAmount());
        bodyDTO.setMoney(money);

        TradeCallBackMoneyDTO fee = new TradeCallBackMoneyDTO();
        fee.setCurrency(cashOrder.getCurrency());
        fee.setAmount(cashOrder.getMerchantFee());
        bodyDTO.setFee(fee);

        TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
        callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
        callBackDTO.setSource(sourceEnum.name());
        callBackDTO.setUrl(finishCashUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }
}
