package app.sphere.command.handler;

import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.cmd.*;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradeWithdrawOrderRepository;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;



@Slf4j
@Component
public class PaymentFinish4WithdrawHandler {

    @Resource
    TradeWithdrawOrderRepository tradeWithdrawOrderRepository;
    @Resource
    SettleAccountCmdService settleAccountCmdService;


    /**
     * 提现（代付）支付结果, 更新状态、结果、时间
     */
    public void handlerPayment4FinishWithdraw(PaymentFinishCommand command) {
        log.info("handlerPayment4FinishWithdraw command={}", JSONUtil.toJsonStr(command));
        doPaymentFinish4Withdraw(command);
    }

    //------------------------------------------------------------------------------------------------------------

    /**
     * 提现（代付）支付后操作
     */
    private boolean doPaymentFinish4Withdraw(PaymentFinishCommand command) {
        //校验参数Command
        checkCommand(command);

        //查询提现订单
        TradeWithdrawOrder order = queryWithdrawOrder(command);

        //处理订单
        handlerWithdrawOrder(command, order);

        //结算-解冻/出款
        PaymentStatusEnum statusEnum = PaymentStatusEnum.codeToEnum(command.getPaymentStatus());
        log.info("handlerPayment4FinishWithdraw statusEnum={}", statusEnum.name());
        doSettleWithdrawOrder(order, command, statusEnum);
        return true;
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
     * 查询提现订单
     */
    private TradeWithdrawOrder queryWithdrawOrder(PaymentFinishCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradeWithdrawOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradeWithdrawOrder::getTradeNo, tradeNo).last("LIMIT 1");
        TradeWithdrawOrder order = tradeWithdrawOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo));

        //下单成功后才操作, 否则需要重复消费
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        boolean equals = TradeStatusEnum.TRADE_SUCCESS.equals(tradeStatusEnum);
        Assert.isTrue(equals, () -> new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo));

        //原订单不能是终态
        boolean contains = PaymentStatusEnum.getFinalStatus().contains(order.getPaymentStatus());
        Assert.isFalse(contains, () -> new PaymentException(ExceptionCode.SYSTEM_BUSY, tradeNo));
        return order;
    }


    /**
     * 处理订单
     */
    private void handlerWithdrawOrder(PaymentFinishCommand command, TradeWithdrawOrder order) {
       //更新订单支付状态、结果、时间
        UpdateWrapper<TradeWithdrawOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(TradeWithdrawOrder::getPaymentStatus, command.getPaymentStatus())
                .set(TradeWithdrawOrder::getPaymentResult, JSONUtil.toJsonStr(command))
                .set(TradeWithdrawOrder::getPaymentFinishTime, order.getPaymentFinishTime())
                .eq(TradeWithdrawOrder::getId, order.getId());
        boolean update = tradeWithdrawOrderRepository.update(updateWrapper);
        log.info("handlerPayment4FinishWithdraw update order result={}", update);
    }


    /**
     * 提现（代付）结算
     */
    private void doSettleWithdrawOrder(TradeWithdrawOrder order, PaymentFinishCommand command,
                                       PaymentStatusEnum paymentStatusEnum) {
        if (PaymentStatusEnum.PAYMENT_SUCCESS.equals(paymentStatusEnum)) {

            //如果此时同步还未返回或者查询时同步未更新完成-判断条件就是交易状态不是成功 -> 则自行计算通道成本和平台利润
            BigDecimal amount = order.getAmount();
            BigDecimal actualAmount = order.getActualAmount();
            BigDecimal merchantFee = order.getMerchantFee();
            BigDecimal accountAmount = order.getAccountAmount();
            BigDecimal channelCost = order.getChannelCost();
            BigDecimal platformProfit = order.getPlatformProfit();
            BigDecimal merchantProfit = order.getMerchantProfit();

            //如果结算需要的金额缺失
            if (Objects.isNull(amount)
                    || Objects.isNull(actualAmount)
                    || Objects.isNull(merchantFee)
                    || Objects.isNull(merchantProfit)
                    || Objects.isNull(accountAmount)
                    || Objects.isNull(channelCost)
                    || Objects.isNull(platformProfit)) {
                log.error("doSettleWithdrawOrder exception {} {} {} {} {} {} {}",
                        amount, actualAmount, merchantFee, merchantProfit, accountAmount, channelCost, platformProfit);
                throw new PaymentException("PaymentFinish4WithdrawHandler exception. some fees is null");
            }

            SettleAccountWithdrawCommand withdrawCommand = new SettleAccountWithdrawCommand();
            BeanUtils.copyProperties(order, withdrawCommand);
            settleAccountCmdService.handlerAccountWithdraw(withdrawCommand);

        } else {

            SettleAccountUpdateUnFrozenCmd unFrozenCmd = new SettleAccountUpdateUnFrozenCmd();
            unFrozenCmd.setTradeNo(order.getTradeNo());
            settleAccountCmdService.handlerAccountUnFrozen(unFrozenCmd);
        }
    }

}
