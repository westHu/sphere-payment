package app.sphere.command.handler;

import app.sphere.command.cmd.SettleAccountUpdateCashCommand;
import app.sphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import cn.hutool.json.JSONUtil;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.TradeModeEnum;
import share.sphere.enums.TradePayoutSourceEnum;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.TradePayoutOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;



@Slf4j
@Component
public class PaymentFinish4PayoutHandler {

    @Resource
    SettleAccountCmdService settleAccountCmdService;

    /**
     * 代付支付结果, 更新状态、结果、时间
     */
    public void handlerPayoutFinish(TradePayoutOrder order) {
        log.info("handlerPayoutFinish order={}", order.getTradeNo());

        //结算-解冻/出款
        PaymentStatusEnum statusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
        log.info("handlerPayoutFinish statusEnum={}", statusEnum.name());
        doSettleCashOrder(order, statusEnum);

        //API订单 执行商户回调
        TradePayoutSourceEnum sourceEnum = TradePayoutSourceEnum.codeToEnum(order.getSource());
        log.info("handlerPayoutFinish sourceEnum={}", sourceEnum.name());
        if (TradePayoutSourceEnum.API.equals(sourceEnum)) {
            TradeCallBackDTO callBackDTO = buildCashOrderCallBackDTO(order, statusEnum, sourceEnum);

            log.info("handlerPayoutFinish callback message: {}", callBackDTO);
        }
    }


    //------------------------------------------------------------------------------------------------------------


    /**
     * 代付结算 - 代付结算，组装数据，消息交互
     */
    private void doSettleCashOrder(TradePayoutOrder order,
                                   PaymentStatusEnum paymentStatusEnum) {
        if (PaymentStatusEnum.PAYMENT_SUCCESS.equals(paymentStatusEnum)) {


            BigDecimal amount = order.getAmount();
            BigDecimal actualAmount = order.getActualAmount();
            BigDecimal merchantFee = order.getMerchantFee();
            BigDecimal merchantProfit = order.getMerchantProfit();
            BigDecimal accountAmount = order.getAccountAmount();
            BigDecimal channelCost = order.getChannelCost();
            BigDecimal platformProfit = order.getPlatformProfit();

            //如果结算需要的金额缺失
            if (Objects.isNull(amount)
                    || Objects.isNull(actualAmount)
                    || Objects.isNull(merchantFee)
                    || Objects.isNull(merchantProfit)
                    || Objects.isNull(accountAmount)
                    || Objects.isNull(channelCost)
                    || Objects.isNull(platformProfit)) {
                log.error("doSettleCashOrder exception {} {} {} {} {} {} {}",
                        amount, actualAmount, merchantFee, merchantProfit, accountAmount, channelCost, platformProfit);
                throw new PaymentException("PaymentFinish4CashHandler exception. some fees is null");
            }

            // 收款清结算
            SettleAccountUpdateCashCommand settleCommand = new SettleAccountUpdateCashCommand();
            BeanUtils.copyProperties(order, settleCommand);
            settleAccountCmdService.handlerAccountPayout(settleCommand);

        } else {
            // 解冻
            SettleAccountUpdateUnFrozenCmd unFrozenCmd = new SettleAccountUpdateUnFrozenCmd();
            unFrozenCmd.setTradeNo(order.getTradeNo());
            settleAccountCmdService.handlerAccountUnFrozen(unFrozenCmd);
        }
    }

    /**
     * 构建回调消息体
     */
    private TradeCallBackDTO buildCashOrderCallBackDTO(TradePayoutOrder order, PaymentStatusEnum statusEnum,
                                                       TradePayoutSourceEnum sourceEnum) {
        Optional<MerchantResultDTO> merchantResultDTO = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult);

        //解析回调地址
        String finishCashUrl = merchantResultDTO.map(MerchantResultDTO::getFinishCashUrl).orElse(null);
        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(order.getTradeNo());
        bodyDTO.setOrderNo(order.getOrderNo());
        bodyDTO.setMerchantId(order.getMerchantId());
        bodyDTO.setMerchantName(order.getMerchantName());
        bodyDTO.setStatus(statusEnum.getName());
        bodyDTO.setTransactionTime(LocalDateTime.now().toString());

        TradeCallBackMoneyDTO moneyDTO = new TradeCallBackMoneyDTO();
        moneyDTO.setCurrency(order.getCurrency());
        moneyDTO.setAmount(order.getAmount());
        bodyDTO.setMoney(moneyDTO);

        TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
        callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
        callBackDTO.setSource(sourceEnum.name());
        callBackDTO.setUrl(finishCashUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }
}
