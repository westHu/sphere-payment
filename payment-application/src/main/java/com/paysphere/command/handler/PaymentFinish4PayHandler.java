package com.paysphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.cmd.PaymentFinishMessageCommand;
import com.paysphere.command.dto.PaymentLinkOrderAttributeDTO;
import com.paysphere.command.dto.TradePaymentAttributeDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import com.paysphere.command.dto.trade.result.MerchantResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.TradePaymentLinkOrder;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.QrCodeTypeEnum;
import com.paysphere.enums.TradeModeEnum;
import com.paysphere.enums.TradePaymentLinkStatusEnum;
import com.paysphere.enums.TradePaymentSourceEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.mq.dto.email.EmailTradePayinReceiptDTO;
import com.paysphere.mq.dto.settle.SettlePayMqMessageDTO;
import com.paysphere.repository.TradePaymentLinkOrderService;
import com.paysphere.repository.TradePaymentOrderService;
import com.paysphere.utils.AmountFormatterUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component
public class PaymentFinish4PayHandler {

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    RedisService redisService;
    @Resource
    TradePaymentLinkOrderService tradePaymentLinkOrderService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 收款支付结果
     */
    public void handlerPaymentFinish4Pay(PaymentFinishMessageCommand command) {
        log.info("handlerPaymentFinish4Pay command={}", JSONUtil.toJsonStr(command));
        String key = TradeConstant.LOCK_PREFIX_PAYMENT_LISTENER + command.getTradeNo();
        redisService.lock(key, () -> doPaymentFinish4Pay(command));
    }


    /**
     * 收款支付完成
     */
    private boolean doPaymentFinish4Pay(PaymentFinishMessageCommand command) {
        String tradeNo = command.getTradeNo();
        Integer paymentStatus = command.getPaymentStatus();

        // 传入的状态也必须是终态
        if (!PaymentStatusEnum.getFinalStatus().contains(paymentStatus)) {
            log.error("doPaymentFinish4Pay payment status must be final status. {}", paymentStatus);
            throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "not final status. refer to " + tradeNo);
        }
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(paymentStatus);
        log.info("handlerPaymentFinish4Pay paymentStatusEnum={}", paymentStatusEnum.name());

        // 判断订单类型, 是否qris静态码类型
        QrCodeTypeEnum qrCodeTypeEnum = QrCodeTypeEnum.codeToEnum(command.getQrCodeType());
        log.info("handlerPaymentFinish4Pay qrCodeTypeEnum={}", qrCodeTypeEnum);
        TradePaymentOrder order = null;
        if (QrCodeTypeEnum.STATIC_CODE.equals(qrCodeTypeEnum)) {
//            order = handlerStaticCodeOrder(command, paymentStatusEnum);
        } else {
            order = handlerPayOrder(command, paymentStatusEnum);
        }

        // 支付成功, 执行结算操作
        if (PaymentStatusEnum.PAYMENT_SUCCESS.equals(paymentStatusEnum)) {
            SettlePayMqMessageDTO messageDTO = buildSettle4PayDTO(order, command);
            SendResult send = rocketMqProducer.syncSend(TradeConstant.SETTLE_PAY_TOPIC, JSONUtil.toJsonStr(messageDTO));
            log.info("handlerPaymentFinish4Pay settle message result={}", send);
            if (!send.getSendStatus().equals(SendStatus.SEND_OK)) {
                log.error("handlerPaymentFinish4Pay settlement message send error. {}", tradeNo);
                throw new PaymentException(ExceptionCode.MESSAGE_MQ_ERROR, tradeNo);
            }
        }

        // API订单/WOOCOMMERCE订单 执行商户回调消息
        TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
        log.info("handlerPaymentFinish4Pay sourceEnum={}", sourceEnum.name());
        if (TradePaymentSourceEnum.callbackEnumList().contains(sourceEnum)) {
            TradeCallBackDTO callBackDTO = buildPayOrderCallBackDTO(order, paymentStatusEnum, sourceEnum);
            SendResult send = rocketMqProducer.syncSend(TradeConstant.TRADE_CALLBACK_TOPIC,
                    JSONUtil.toJsonStr(callBackDTO));
            log.info("handlerPaymentFinish4Pay callback message result={}", send);
            if (!send.getSendStatus().equals(SendStatus.SEND_OK)) {
                log.error("handlerPaymentFinish4Pay callback message send error. {}", tradeNo);
                throw new PaymentException(ExceptionCode.MESSAGE_MQ_ERROR, tradeNo);
            }
        }
        return true;
    }


    /**
     * 其他正常订单的处理
     */
    private TradePaymentOrder handlerPayOrder(PaymentFinishMessageCommand command, PaymentStatusEnum statusEnum) {
        String tradeNo = command.getTradeNo();

        // 查询收款订单
        QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderService.getOne(queryWrapper);
        if (Objects.isNull(order)) {
            log.error("doPaymentFinish4Pay pay order not exist, tradeNo={}", tradeNo);
            throw new PaymentException(ExceptionCode.PAY_ORDER_NOT_EXIST, tradeNo);
        }

        // 交易状态必须是成功，其实收款基本上是tradeStatus更新成功后才会接收到回调
        /*TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        if (!TradeStatusEnum.TRADE_SUCCESS.equals(tradeStatusEnum)) {
            log.error("doPaymentFinish4Pay tradeStatus not success, tradeStatusEnum={}", tradeStatusEnum);
            throw new PaymentException(ExceptionCode.MESSAGE_CONSUMER_LATER);
        }*/

        // 已经成功或者失败等终态
        Integer paymentStatus = order.getPaymentStatus();
        if (PaymentStatusEnum.getFinalStatus().contains(paymentStatus)) {
            log.error("doPaymentFinish4Pay pay order already final, paymentStatus={}", paymentStatus);
            throw new PaymentException(ExceptionCode.PAY_ORDER_FINAL_STATUS, tradeNo);
        }

        // 更新订单支付状态、结果、时间
//        order.setPaymentFinishTime(LocalDateTime.parse(command.getTransactionTime(), TradeConstant.DF_0));
        UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
        payOrderUpdate.lambda()
                .set(TradePaymentOrder::getPaymentStatus, statusEnum.getCode())
                .set(TradePaymentOrder::getPaymentResult, JSONUtil.toJsonStr(command))
                .set(TradePaymentOrder::getPaymentFinishTime, order.getPaymentFinishTime())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePaymentOrder::getId, order.getId());
        boolean update = tradePaymentOrderService.update(payOrderUpdate);
        log.info("handlerPaymentFinish4Pay update order result={}", update);

        // 如果是PaymentLink
        boolean isPaymentLink = Optional.ofNullable(order.getSource())
                .map(e -> e.equals(TradePaymentSourceEnum.PAY_LINK.getCode()))
                .orElse(false);
        log.info("handlerPaymentFinish4Pay isPaymentLink? {}", isPaymentLink);
        if (isPaymentLink) {
            QueryWrapper<TradePaymentLinkOrder> linkOrderQuery = new QueryWrapper<>();
            linkOrderQuery.lambda().eq(TradePaymentLinkOrder::getLinkNo, order.getOrderNo()).last(TradeConstant.LIMIT_1);
            TradePaymentLinkOrder paymentLinkOrder = tradePaymentLinkOrderService.getOne(linkOrderQuery);
            if (Objects.nonNull(paymentLinkOrder)) {
                TradePaymentLinkStatusEnum paymentLinkStatusEnum = Optional.of(command)
                        .map(PaymentFinishMessageCommand::getPaymentStatus)
                        .map(TradePaymentLinkStatusEnum::codeToEnum)
                        .orElse(TradePaymentLinkStatusEnum.PAYMENT_LINK_SUCCESS);
                log.info("handlerPaymentFinish4Pay paymentLinkStatusEnum={}", paymentLinkStatusEnum.name());

                UpdateWrapper<TradePaymentLinkOrder> linkOrderUpdate = new UpdateWrapper<>();
                linkOrderUpdate.lambda()
                        .set(TradePaymentLinkOrder::getLinkStatus, paymentLinkStatusEnum.getCode())
                        .eq(TradePaymentLinkOrder::getId, paymentLinkOrder.getId());
                boolean linkUpdate = tradePaymentLinkOrderService.update(linkOrderUpdate);
                log.info("handlerPaymentFinish4Pay update paymentLink result={}", linkUpdate);

                // 邮箱通知
                List<String> emailList = Optional.of(paymentLinkOrder).map(TradePaymentLinkOrder::getAttribute)
                        .map(e -> JSONUtil.toBean(e, PaymentLinkOrderAttributeDTO.class))
                        .map(PaymentLinkOrderAttributeDTO::getNotificationEmail)
                        .orElse(null);
                if (CollectionUtils.isNotEmpty(emailList)) {
                    EmailTradePayinReceiptDTO receiptDTO = new EmailTradePayinReceiptDTO();
                    receiptDTO.setTradeNo(order.getTradeNo());
                    receiptDTO.setPaymentMethod(paymentLinkOrder.getPaymentMethod());
                    receiptDTO.setAmount(AmountFormatterUtil.formatAmount(paymentLinkOrder.getAmount()));
                    receiptDTO.setMerchantId(paymentLinkOrder.getMerchantId());
                    receiptDTO.setMerchantName(paymentLinkOrder.getMerchantName());
//                    receiptDTO.setTransactionTime(order.getPaymentFinishTime().format(TradeConstant.DF_5));
                    receiptDTO.setRemark(paymentLinkOrder.getNotes());
                    for (String email : emailList) {
                        rocketMqProducer.syncSendTradePayinReceiptEmail(email,
                                "payspherepay.id You've Received Payment?", receiptDTO);
                    }
                }
            }
        }
        return order;
    }


    /**
     * 构建主动清结算参数
     */
    private SettlePayMqMessageDTO buildSettle4PayDTO(TradePaymentOrder order, PaymentFinishMessageCommand command) {
        // 如果此时同步还未返回，则自行计算通道成本和平台利润
        BigDecimal amount = order.getAmount();
        BigDecimal merchantFee = order.getMerchantFee();
        BigDecimal merchantProfit = order.getMerchantProfit();
        BigDecimal channelCost = order.getChannelCost();
        BigDecimal platformProfit = order.getPlatformProfit();

        // 需要考虑提前回调的情况
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        log.info("buildSettle4PayDTO paymentStatusEnum={}", paymentStatusEnum);
        if (!PaymentStatusEnum.PAYMENT_PROCESSING.equals(paymentStatusEnum)) {
            channelCost = command.getChannelCost();
            platformProfit = merchantFee.subtract(merchantProfit).subtract(channelCost);
        }

        MerchantResultDTO merchantResult = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .orElseThrow(() -> new PaymentException("buildSettle4PayDTO no merchantResult"));

//        LocalDateTime paymentFinishTime = Objects.nonNull(order.getPaymentFinishTime())
//                ? order.getPaymentFinishTime()
//                : LocalDateTime.now();

        SettlePayMqMessageDTO messageDTO = new SettlePayMqMessageDTO();
        messageDTO.setBusinessNo(order.getBusinessNo());
        messageDTO.setTradeNo(order.getTradeNo());
        messageDTO.setOuterNo(order.getOrderNo());
//        messageDTO.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
//        messageDTO.setPaymentFinishTime(paymentFinishTime.format(TradeConstant.DF_0));

        messageDTO.setCurrency(order.getCurrency());
        messageDTO.setAmount(amount);
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
        messageDTO.setSettleType(merchantResult.getSettleType());
        messageDTO.setSettleTime(merchantResult.getSettleTime());
        return messageDTO;
    }


    /**
     * 构建回调消息体
     */
    private TradeCallBackDTO buildPayOrderCallBackDTO(TradePaymentOrder order, PaymentStatusEnum statusEnum,
                                                      TradePaymentSourceEnum sourceEnum) {
        String finishPaymentUrl = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getFinishPaymentUrl).orElse(null);

        String shopName = Optional.of(order).map(TradePaymentOrder::getAttribute).map(e -> JSONUtil.toBean(e,
                TradePaymentAttributeDTO.class)).map(TradePaymentAttributeDTO::getShopName).orElse(null);

//        ZonedDateTime tradeTime = Optional.of(order)
//                .map(TradePaymentOrder::getTradeTime)
////                .map(e -> e.atZone(TradeConstant.ZONE_ID))
//                .orElse(ZonedDateTime.now());

        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(order.getTradeNo());
        bodyDTO.setOrderNo(order.getOrderNo());
        bodyDTO.setMerchantId(order.getMerchantId());
        bodyDTO.setMerchantName(order.getMerchantName());
        bodyDTO.setShopName(shopName);
//        bodyDTO.setStatus(statusEnum.getMerchantStatus());
//        bodyDTO.setTransactionTime(tradeTime.format(TradeConstant.DF_3));

        TradeCallBackMoneyDTO money = new TradeCallBackMoneyDTO();
        money.setCurrency(order.getCurrency());
        money.setAmount(order.getAmount());
        bodyDTO.setMoney(money);

        TradeCallBackMoneyDTO fee = new TradeCallBackMoneyDTO();
        fee.setCurrency(order.getCurrency());
        fee.setAmount(order.getMerchantFee());
        bodyDTO.setFee(fee);

        TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
        callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
        callBackDTO.setSource(sourceEnum.name());
        callBackDTO.setUrl(finishPaymentUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }


}
