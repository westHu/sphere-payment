package app.sphere.command.impl;

import app.sphere.assembler.ApplicationConverter;
import app.sphere.command.*;
import app.sphere.command.cmd.*;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.SettleOrderRepository;
import infrastructure.sphere.db.entity.SettleOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.enums.*;
import share.sphere.exception.PaymentException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;

import static share.sphere.TradeConstant.LIMIT_1;
import static share.sphere.enums.SettleStatusEnum.SETTLE_FAILED;
import static share.sphere.enums.SettleStatusEnum.SETTLE_TODO;

@Slf4j
@Service
public class SettleOrderCmdServiceImpl implements SettleOrderCmdService {

    @Resource
    SettleOrderRepository settleOrderRepository;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettlePaymentCmdService settlePaymentCmdService;
    @Resource
    SettlePayoutCmdService settlePayoutCmdService;
    @Resource
    ApplicationConverter applicationConverter;


    @Override
    public boolean supplement(SettleSupplementCmd command) {
        log.info("supplement command={}", JSONUtil.toJsonStr(command));
        return doSupplement(command);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refund(SettleRefundCmd command) {
        log.info("refund command={}", JSONUtil.toJsonStr(command));
        return doRefund(command);
    }


    //---------------------------------------------------------------------------------------------------------

    private boolean doSupplement(SettleSupplementCmd command) {
        String tradeNo = command.getTradeNo();

        //如果结算单已经存在，异常
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder settleOrder = settleOrderRepository.getOne(queryWrapper);
        Assert.isNull(settleOrder, () -> new PaymentException("Can not settle again"));

        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(tradeNo);
        log.info("doSupplement tradeTypeEnum={}", tradeTypeEnum);

        if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
            doPaymentSupplement(command);
        } else if (TradeTypeEnum.PAYOUT.equals(tradeTypeEnum)) {
            doPayoutSupplement(command);
        }

        throw new PaymentException("trade type not support. " + tradeTypeEnum);
    }

    /**
     * 收款补单
     */
    private void doPaymentSupplement(SettleSupplementCmd command) {
        String tradeNo = command.getTradeNo();

        //结算类型
        SettleTypeEnum settleTypeEnum = SettleTypeEnum.valueOf(command.getSettleType());
        log.info("doPaymentSupplement tradeNo={} settleTypeEnum={}", tradeNo, settleTypeEnum.name());
        SettlePaymentCommand payCommand = applicationConverter.convertSettlePayMessageCommand(command);

        if (SettleTypeEnum.T1.equals(settleTypeEnum)) {
            settlePaymentCmdService.handlerSettleImmediate(payCommand);
        } else if (SettleTypeEnum.T0.equals(settleTypeEnum)){
            DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
            log.info("settlePayListener tradeNo={} dayOfWeek={}", tradeNo, dayOfWeek);
            if (DayOfWeek.SUNDAY.equals(dayOfWeek) || DayOfWeek.SATURDAY.equals(dayOfWeek)) {
                settlePaymentCmdService.addSettleOrder(payCommand, SETTLE_TODO);
            } else {
                settlePaymentCmdService.handlerSettleImmediate(payCommand);
            }
        } else {
            settlePaymentCmdService.addSettleOrder(payCommand, SETTLE_TODO);
        }
    }

    /**
     * 代付补单
     */
    private void doPayoutSupplement(SettleSupplementCmd command) {
        String tradeNo = command.getTradeNo();

        SettlePayoutCommand payoutCommand = applicationConverter.convertSettlePayoutMessageCommand(command);
        payoutCommand.setTradeNo(tradeNo);
        payoutCommand.setSettleType(SettleTypeEnum.T0.name());
        settlePayoutCmdService.handlerSettleImmediate(payoutCommand);
    }

    /**
     * 退单操作
     */
    private boolean doRefund(SettleRefundCmd command) {
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(command.getTradeNo());
        log.info("doRefund tradeTypeEnum={}", tradeTypeEnum);

        if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
            doPaymentRefund(command);
        } else if (TradeTypeEnum.PAYOUT.equals(tradeTypeEnum)) {
            doPayoutRefund(command);
        } else {
            throw new PaymentException("trade type not support. " + tradeTypeEnum);
        }
        return true;
    }

    /**
     * 收款退款
     */
    private void doPaymentRefund(SettleRefundCmd command) {
        String tradeNo = command.getTradeNo();

        //查询结算
        QueryWrapper<SettleOrder> settleQuery = new QueryWrapper<>();
        settleQuery.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder settleOrder = settleOrderRepository.getOne(settleQuery);

        //1、没有结算订单 - 概率较小 - 直接取消
        if (Objects.isNull(settleOrder)) {
            saveCancelSettleOrder(command, TradeTypeEnum.PAYMENT);
            return;
        }

        SettleStatusEnum settleStatusEnum = SettleStatusEnum.codeToEnum(settleOrder.getSettleStatus());
        log.info("doPaymentRefund tradeNo={} settleStatusEnum={}", tradeNo, settleStatusEnum.name());

        //1.5、结算失败不能进行退单
        if (SETTLE_FAILED.equals(settleStatusEnum)) {
            return;
        }

        //2、未结算/待结算单已经生成/结算失败- 定时任务、D1、T1/各类原因
        if (SETTLE_TODO.equals(settleStatusEnum)) {
            //商户退款 从待结算金额中退款 退款金额该是商户的到账金额
            //平台退款 从待结算金额中对抗 退款金额该是平台利润金额
            BigDecimal accountAmount = settleOrder.getAccountAmount(); //退到账金额
            BigDecimal platformProfit = settleOrder.getPlatformProfit(); //退平台利润
            BigDecimal merchantProfit = settleOrder.getMerchantProfit();

            SettleAccountUpdateRefundCommand merchantRefundCommand = new SettleAccountUpdateRefundCommand();
            merchantRefundCommand.setTradeNo(settleOrder.getTradeNo());
            merchantRefundCommand.setMerchantId(settleOrder.getMerchantId());
            merchantRefundCommand.setMerchantName(settleOrder.getMerchantName());
            merchantRefundCommand.setAccountNo(settleOrder.getAccountNo());
            merchantRefundCommand.setCurrency(settleOrder.getCurrency());
            merchantRefundCommand.setRefundAccountAmount(accountAmount); //退款商户到账金额
            merchantRefundCommand.setRefundPlatformProfit(platformProfit); //退款平台金利润
            merchantRefundCommand.setRefundMerchantProfit(merchantProfit);//代理商分润
            merchantRefundCommand.setAccountOptType(AccountOptTypeEnum.REFUND);
            settleAccountCmdService.handlerAccountToSettleRefund(merchantRefundCommand);
            log.info("doPaymentRefund tradeNo={}", tradeNo);

            //更新结算取消状态, 如果该订单正在结算,则有异常情况
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderRepository.update(settleUpdate);
            return;
        }

        //3、已结算、结算正在处理 - 则抛出异常，等会处理，等会要么成功，要么失败，!!! 当然也有小概率一直在处理中
        if (SettleStatusEnum.SETTLE_PROCESSING.equals(settleStatusEnum)) {
            throw new PaymentException("doPaymentRefund settle processing");
        }

        //4、已结算、结算成功，则进行退款
        if (SettleStatusEnum.SETTLE_SUCCESS.equals(settleStatusEnum)) {
            //商户退款 从可用金额中退款 退款金额该是商户的到账金额
            //平台退款 从可用金额中退款 退款金额该是平台利润金额
            BigDecimal accountAmount = settleOrder.getAccountAmount(); //退到账金额
            BigDecimal platformProfit = settleOrder.getPlatformProfit(); //退平台利润
            BigDecimal merchantProfit = settleOrder.getMerchantProfit();

            SettleAccountUpdateRefundCommand merchantRefundCommand = new SettleAccountUpdateRefundCommand();
            merchantRefundCommand.setTradeNo(settleOrder.getTradeNo());
            merchantRefundCommand.setMerchantId(settleOrder.getMerchantId());
            merchantRefundCommand.setMerchantName(settleOrder.getMerchantName());
            merchantRefundCommand.setAccountNo(settleOrder.getAccountNo());
            merchantRefundCommand.setCurrency(settleOrder.getCurrency());
            merchantRefundCommand.setRefundAccountAmount(accountAmount); //到账金额
            merchantRefundCommand.setRefundPlatformProfit(platformProfit); //平台利润
            merchantRefundCommand.setRefundMerchantProfit(merchantProfit);//代理商分润
            merchantRefundCommand.setAccountOptType(AccountOptTypeEnum.REFUND);
            settleAccountCmdService.handlerAccountSettleRefund(merchantRefundCommand);
            log.info("doPaymentRefund tradeNo={} handlerAccountSettleRefund", tradeNo);

            //更新结算取消状态
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderRepository.update(settleUpdate);
        }

    }

    /**
     * 代付退款
     */
    private void doPayoutRefund(SettleRefundCmd command) {
        String tradeNo = command.getTradeNo();

        //查询结算
        QueryWrapper<SettleOrder> settleQuery = new QueryWrapper<>();
        settleQuery.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder settleOrder = settleOrderRepository.getOne(settleQuery);

        //代付是同步的，能退款，肯定结算冻结已经执行过 - 较小概率
        if (Objects.isNull(settleOrder)) {
            saveCancelSettleOrder(command, TradeTypeEnum.PAYOUT);
            return;
        }

        //已结算 已结算失败， 解冻冻结金额
        SettleStatusEnum settleStatusEnum = SettleStatusEnum.codeToEnum(settleOrder.getSettleStatus());
        log.info("doPayoutRefund tradeNo={} settleStatusEnum={}", tradeNo, settleStatusEnum);

        if (SETTLE_TODO.equals(settleStatusEnum) || SETTLE_FAILED.equals(settleStatusEnum)) {

            //商户解冻
            SettleAccountUpdateUnFrozenCmd unfrozenCommand = new SettleAccountUpdateUnFrozenCmd();
            unfrozenCommand.setTradeNo(settleOrder.getTradeNo());
            settleAccountCmdService.handlerAccountUnFrozen(unfrozenCommand);

            //更新结算为取消
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderRepository.update(settleUpdate);
        }

        //已结算 已结算成功，代付结算肯定是已经成功的
        if (SettleStatusEnum.SETTLE_SUCCESS.equals(settleStatusEnum)) {
            //商户退款, 退款金额该是商户的到账金额
            //平台退款 退款金额该是平台利润金额
            //此处退款是实扣金额，是加上（accountAmount + merchantFee）的金额，如果有代理商则还加上代理商金额
            BigDecimal accountAmount = settleOrder.getAccountAmount();
            BigDecimal merchantFee = settleOrder.getMerchantFee();
            BigDecimal merchantRefundAmount = accountAmount.add(merchantFee); //实扣金额
            BigDecimal platformProfit = settleOrder.getPlatformProfit();
            BigDecimal merchantProfit = settleOrder.getMerchantProfit();

            SettleAccountUpdateRefundCommand merchantRefundCommand = new SettleAccountUpdateRefundCommand();
            merchantRefundCommand.setTradeNo(settleOrder.getTradeNo());
            merchantRefundCommand.setMerchantId(settleOrder.getMerchantId());
            merchantRefundCommand.setMerchantName(settleOrder.getMerchantName());
            merchantRefundCommand.setAccountNo(settleOrder.getAccountNo());
            merchantRefundCommand.setCurrency(settleOrder.getCurrency());
            merchantRefundCommand.setRefundAccountAmount(merchantRefundAmount.negate());//取负数
            merchantRefundCommand.setRefundPlatformProfit(platformProfit);
            merchantRefundCommand.setRefundMerchantProfit(merchantProfit);//代理商分润
            merchantRefundCommand.setAccountOptType(AccountOptTypeEnum.REFUND);
            settleAccountCmdService.handlerAccountSettleRefund(merchantRefundCommand);
            log.info("doPayoutRefund tradeNo={}", tradeNo);

            //结算状态更新为取消
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderRepository.update(settleUpdate);
        }
    }

    /**
     * 保存取消的结算订单
     */
    private void saveCancelSettleOrder(SettleRefundCmd command, TradeTypeEnum tradeTypeEnum) {
        String tradeNo = command.getTradeNo();
        String alphanumeric4 = RandomStringUtils.randomAlphanumeric(4);
        String alphanumeric8 = RandomStringUtils.randomAlphanumeric(8);

        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setSettleType(SettleTypeEnum.T0.name());
        settleOrder.setTradeNo(tradeNo);
        settleOrder.setTradeType(tradeTypeEnum.getCode());
        settleOrder.setTradeTime(System.currentTimeMillis());
        settleOrder.setPaymentFinishTime(System.currentTimeMillis());
        settleOrder.setMerchantId(alphanumeric8);
        settleOrder.setMerchantName(alphanumeric8);
        settleOrder.setAccountNo(alphanumeric8);
        settleOrder.setChannelCode(alphanumeric4);
        settleOrder.setChannelName(alphanumeric4);
        settleOrder.setPaymentMethod(alphanumeric4);
        settleOrder.setDeductionType(DeductionTypeEnum.DEDUCTION_INTERNAL.getCode());
        settleOrder.setCurrency(CurrencyEnum.IDR.name());
        settleOrder.setAmount(BigDecimal.ZERO);
        settleOrder.setMerchantFee(BigDecimal.ZERO);
        settleOrder.setMerchantProfit(BigDecimal.ZERO);
        settleOrder.setAccountAmount(BigDecimal.ZERO);
        settleOrder.setChannelCost(BigDecimal.ZERO);
        settleOrder.setPlatformProfit(BigDecimal.ZERO);
        settleOrder.setSettleStatus(SettleStatusEnum.SETTLE_CANCEL.getCode());
        settleOrder.setAttribute("{}");
        settleOrder.setCreateTime(LocalDateTime.now());
        settleOrderRepository.save(settleOrder);
    }

}
