package app.sphere.command.handler;

import app.sphere.command.SettlePaymentCmdService;
import app.sphere.command.cmd.SettlePaymentCommand;
import app.sphere.command.dto.trade.callback.*;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import cn.hutool.json.JSONUtil;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import share.sphere.enums.*;
import share.sphere.exception.PaymentException;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static share.sphere.enums.SettleStatusEnum.SETTLE_TODO;


@Slf4j
@Component
public class PaymentFinish4PaymentHandler {

    @Resource
    SettlePaymentCmdService settlePaymentCmdService;

    /**
     * 代收支付结果
     */
    public void handlerPaymentFinish4Payment(TradePaymentOrder order) {
        log.info("handlerPaymentFinish4Payment order={}", order.getTradeNo());

        //支付成功, 执行结算操作
        PaymentStatusEnum statusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        log.info("handlerPaymentFinish4Payment statusEnum={}", statusEnum.name());
        if (PaymentStatusEnum.PAYMENT_SUCCESS.equals(statusEnum)) {
            handleSettle(order);
        }

        //API订单 执行商户回调消息
        TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
        log.info("handlerPaymentFinish4Payment sourceEnum={}", sourceEnum.name());
        TradeCallBackDTO callBackDTO = buildPayOrderCallBackDTO(order, statusEnum, sourceEnum);
    }


    //------------------------------------------------------------------------------------------------------------

    /**
     * 构建主动清结算参数
     */
    private void handleSettle(TradePaymentOrder order) {
        //参数
        BigDecimal amount = order.getAmount();
        BigDecimal merchantFee = order.getMerchantFee();
        BigDecimal merchantProfit = order.getMerchantProfit();
        BigDecimal accountAmount = order.getAccountAmount();
        BigDecimal channelCost = order.getChannelCost();
        BigDecimal platformProfit = order.getPlatformProfit();

        //如果结算需要的金额缺失
        if (Objects.isNull(amount)
                ||Objects.isNull(merchantFee)
                || Objects.isNull(merchantProfit)
                || Objects.isNull(accountAmount)
                || Objects.isNull(channelCost)
                || Objects.isNull(platformProfit)) {
            log.error("handleSettle exception {} {} {} {} {} {}",
                    amount, merchantFee, merchantProfit, accountAmount, channelCost, platformProfit);
            throw new PaymentException("PaymentFinish4PaymentHandler exception. some fees is null");
        }


        SettleTypeEnum settleTypeEnum = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getSettleType)
                .map(SettleTypeEnum::valueOf)
                .orElse(SettleTypeEnum.T0);

        SettlePaymentCommand settlePaymentCommand = new SettlePaymentCommand();
        BeanUtils.copyProperties(order, settlePaymentCommand);
        if (SettleTypeEnum.T1.equals(settleTypeEnum)) {
            settlePaymentCmdService.handlerSettleImmediate(settlePaymentCommand);
        } else if (SettleTypeEnum.T0.equals(settleTypeEnum)){
            DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
            if (DayOfWeek.SUNDAY.equals(dayOfWeek) || DayOfWeek.SATURDAY.equals(dayOfWeek)) {
                settlePaymentCmdService.addSettleOrder(settlePaymentCommand, SETTLE_TODO);
            } else {
                settlePaymentCmdService.handlerSettleImmediate(settlePaymentCommand);
            }
        } else {
            settlePaymentCmdService.addSettleOrder(settlePaymentCommand, SETTLE_TODO);
        }
    }

    /**
     * 构建回调消息体
     */
    private TradeCallBackDTO buildPayOrderCallBackDTO(TradePaymentOrder payOrder, PaymentStatusEnum statusEnum,
                                                      TradePaymentSourceEnum sourceEnum) {
        String finishPaymentUrl = Optional.of(payOrder).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getFinishPaymentUrl)
                .orElse(null);

        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(payOrder.getTradeNo());
        bodyDTO.setOrderNo(payOrder.getOrderNo());
        bodyDTO.setMerchantId(payOrder.getMerchantId());
        bodyDTO.setMerchantName(payOrder.getMerchantName());
        bodyDTO.setPaymentMethod(payOrder.getPaymentMethod());
        bodyDTO.setStatus(statusEnum.getName());
        bodyDTO.setTransactionTime(null);

        TradeCallBackMoneyDTO moneyDTO = new TradeCallBackMoneyDTO();
        moneyDTO.setCurrency(payOrder.getCurrency());
        moneyDTO.setAmount(payOrder.getAmount());
        bodyDTO.setMoney(moneyDTO);

        TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
        callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
        callBackDTO.setSource(sourceEnum.name());
        callBackDTO.setUrl(finishPaymentUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }
}
