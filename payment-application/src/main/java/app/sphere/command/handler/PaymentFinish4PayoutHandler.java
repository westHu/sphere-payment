package app.sphere.command.handler;

import app.sphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.cmd.PaymentFinishCommand;
import app.sphere.command.cmd.SettleAccountUpdateSettleCommand;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.TradeModeEnum;
import share.sphere.enums.TradePayoutSourceEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.exception.ExceptionCode;
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
    TradePayoutOrderRepository tradePayoutOrderRepository;
    @Resource
    SettleAccountCmdService settleAccountCmdService;

    /**
     * 代付支付结果, 更新状态、结果、时间
     */
    public void handlerPayoutFinish(PaymentFinishCommand command) {
        log.info("handlerPayoutFinish command={}", JSONUtil.toJsonStr(command));
        doPaymentFinish4Payout(command);
    }


    //------------------------------------------------------------------------------------------------------------

    /**
     * 代付支付后操作
     */
    private void doPaymentFinish4Payout(PaymentFinishCommand command) {
        //校验参数Command
        checkCommand(command);

        //查询代收订单
        TradePayoutOrder order = queryPayoutOrder(command);

        //处理订单
        handlerPayoutOrder(command, order);

        //结算-解冻/出款
        PaymentStatusEnum statusEnum = PaymentStatusEnum.codeToEnum(command.getPaymentStatus());
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

    /**
     * 校验参数Command
     */
    private void checkCommand(PaymentFinishCommand command) {
        String tradeNo = command.getTradeNo();
        //传入的状态也必须是终态
        boolean contains = PaymentStatusEnum.getFinalStatus().contains(command.getPaymentStatus());
        Assert.isTrue(contains, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "not final status. TradeNo to " + tradeNo));
    }

    /**
     * 查询代付订单
     */
    private TradePayoutOrder queryPayoutOrder(PaymentFinishCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradePayoutOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last("LIMIT 1");
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));

        //下单成功后才操作, 否则需要重复消费
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        boolean equals = TradeStatusEnum.TRADE_SUCCESS.equals(tradeStatusEnum);
        Assert.isTrue(equals, () -> new PaymentException("CALLBACK_ORDER_NOT_BEEN_PLACED"));

        //原订单不能是终态
        boolean contains = PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus());
        Assert.isFalse(contains, () -> new PaymentException(ExceptionCode.BUSINESS_PARAM_ERROR, tradeNo));
        return order;
    }

    /**
     * 处理订单
     */
    private void handlerPayoutOrder(PaymentFinishCommand command, TradePayoutOrder order) {
        //更新订单支付状态、结果、时间
        order.setPaymentFinishTime(System.currentTimeMillis());
        UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
        cashOrderUpdate.lambda()
                .set(TradePayoutOrder::getPaymentStatus, command.getPaymentStatus())
                .set(TradePayoutOrder::getPaymentResult, JSONUtil.toJsonStr(command))
                .set(TradePayoutOrder::getPaymentFinishTime, order.getPaymentFinishTime())
                .eq(TradePayoutOrder::getId, order.getId());
        boolean update = tradePayoutOrderRepository.update(cashOrderUpdate);
        log.info("handlerPayoutFinish update order result={}", update);
    }


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
            SettleAccountUpdateSettleCommand settleCommand = new SettleAccountUpdateSettleCommand();
            BeanUtils.copyProperties(order, settleCommand);
            settleAccountCmdService.handlerAccountSettlement(settleCommand);

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
