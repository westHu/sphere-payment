package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.paysphere.assembler.ApplicationConverter;
import com.paysphere.cache.RedisService;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.SettleCashCmdService;
import com.paysphere.command.SettlePayCmdService;
import com.paysphere.command.SettleRefundCmdService;
import com.paysphere.command.cmd.SettleAccountUpdateRefundCommand;
import com.paysphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import com.paysphere.command.cmd.SettleCashMessageCommand;
import com.paysphere.command.cmd.SettlePayMessageCommand;
import com.paysphere.command.cmd.SettleRefundCmd;
import com.paysphere.command.cmd.SettleSupplementCmd;
import com.paysphere.command.dto.MerchantProfitDTO;
import com.paysphere.command.dto.SettleAttributeDTO;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.enums.AccountOptTypeEnum;
import com.paysphere.enums.CurrencyEnum;
import com.paysphere.enums.DeductionTypeEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.SettleTypeEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.SettleOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.paysphere.TradeConstant.LIMIT_1;
import static com.paysphere.enums.SettleStatusEnum.SETTLE_FAILED;
import static com.paysphere.enums.SettleStatusEnum.SETTLE_TODO;

@Slf4j
@Service
public class SettleRefundCmdServiceImpl implements SettleRefundCmdService {

    @Resource
    RedisService redisService;
    @Resource
    SettleOrderService settleOrderService;
    @Resource
    SettleAccountCmdService settleAccountCmdService;
    @Resource
    SettlePayCmdService settlePayCmdService;
    @Resource
    SettleCashCmdService settleCashCmdService;
    @Resource
    ApplicationConverter applicationConverter;


    @Override
    public boolean handlerSupplement(SettleSupplementCmd command) {
        log.info("handlerSupplement command={}", JSONUtil.toJsonStr(command));
        return redisService.lock("LOCK_PREFIX_REFUND" + command.getTradeNo(), () -> doSupplement(command));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlerRefund(SettleRefundCmd command) {
        log.info("handlerRefund command={}", JSONUtil.toJsonStr(command));
        return redisService.lock("LOCK_PREFIX_REFUND" + command.getTradeNo(), () -> doRefund(command));
    }


    //---------------------------------------------------------------------------------------------------------

    private boolean doSupplement(SettleSupplementCmd command) {
        String tradeNo = command.getTradeNo();

        //如果结算单已经存在，异常
        QueryWrapper<SettleOrder> settleOrderQuery = new QueryWrapper<>();
        settleOrderQuery.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder settleOrder = settleOrderService.getOne(settleOrderQuery);
        Assert.isNull(settleOrder, () -> new PaymentException("Settle order already exists and cannot be settled again"));

        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(tradeNo);
        log.info("doSupplement tradeTypeEnum={}", tradeTypeEnum);

        if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
            doSupplement4Pay(command);
        } else if (TradeTypeEnum.PAYOUT.equals(tradeTypeEnum)) {
            doSupplement4Cash(command);
        } else {
            throw new PaymentException("trade type not support. " + tradeTypeEnum);
        }
        return true;
    }

    /**
     * 收款补单
     */
    private void doSupplement4Pay(SettleSupplementCmd command) {
        String tradeNo = command.getTradeNo();

        //结算类型
        SettleTypeEnum settleTypeEnum = SettleTypeEnum.valueOf(command.getSettleType());
        log.info("doSupplement4Pay tradeNo={} settleTypeEnum={}", tradeNo, settleTypeEnum.name());
        SettlePayMessageCommand payCommand = applicationConverter.convertSettlePayMessageCommand(command);

        if (SettleTypeEnum.D0.equals(settleTypeEnum)) {
            settlePayCmdService.handlerSettleImmediate(payCommand);

        } else if (SettleTypeEnum.T0.equals(settleTypeEnum)){
            DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
            log.info("settlePayListener tradeNo={} dayOfWeek={}", tradeNo, dayOfWeek);
            if (DayOfWeek.SUNDAY.equals(dayOfWeek) || DayOfWeek.SATURDAY.equals(dayOfWeek)) {
                settlePayCmdService.addSettleOrder(payCommand, SETTLE_TODO);
            } else {
                settlePayCmdService.handlerSettleImmediate(payCommand);
            }

        } else {
            settlePayCmdService.addSettleOrder(payCommand, SETTLE_TODO);
        }
    }

    /**
     * 代付补单
     */
    private void doSupplement4Cash(SettleSupplementCmd command) {
        String tradeNo = command.getTradeNo();

        SettleCashMessageCommand cashCommand = applicationConverter.convertSettleCashMessageCommand(command);
        cashCommand.setSettleType(SettleTypeEnum.D0.name());
        settleCashCmdService.handlerSettleImmediate(cashCommand);
    }

    /**
     * 退单操作
     */
    private boolean doRefund(SettleRefundCmd command) {
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(command.getTradeNo());
        log.info("doRefund tradeTypeEnum={}", tradeTypeEnum);

        if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
            doRefund4Pay(command);

        } else if (TradeTypeEnum.PAYOUT.equals(tradeTypeEnum)) {
            doRefund4Cash(command);

        } else {
            throw new PaymentException("trade type not support. " + tradeTypeEnum);
        }
        return true;
    }

    /**
     * 收款退款
     */
    private void doRefund4Pay(SettleRefundCmd command) {
        String tradeNo = command.getTradeNo();

        //查询结算
        QueryWrapper<SettleOrder> settleQuery = new QueryWrapper<>();
        settleQuery.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder settleOrder = settleOrderService.getOne(settleQuery);

        //1、没有结算订单 - 概率较小 - 直接取消
        if (Objects.isNull(settleOrder)) {
            saveCancelSettleOrder(command, TradeTypeEnum.PAYMENT);
            return;
        }

        SettleStatusEnum settleStatusEnum = SettleStatusEnum.codeToEnum(settleOrder.getSettleStatus());
        log.info("doRefund4Pay tradeNo={} settleStatusEnum={}", tradeNo, settleStatusEnum.name());

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
            List<MerchantProfitDTO> merchantProfitList = Optional.of(settleOrder).map(SettleOrder::getAttribute)
                    .map(e -> JSONUtil.toBean(e, SettleAttributeDTO.class))
                    .map(SettleAttributeDTO::getMerchantProfitList).orElse(null);

            SettleAccountUpdateRefundCommand merchantRefundCommand = new SettleAccountUpdateRefundCommand();
            merchantRefundCommand.setTradeNo(settleOrder.getTradeNo());
            merchantRefundCommand.setOuterNo(settleOrder.getOuterNo());
            merchantRefundCommand.setMerchantId(settleOrder.getMerchantId());
            merchantRefundCommand.setMerchantName(settleOrder.getMerchantName());
            merchantRefundCommand.setAccountNo(settleOrder.getAccountNo());
            merchantRefundCommand.setCurrency(settleOrder.getCurrency());
            merchantRefundCommand.setRefundAccountAmount(accountAmount); //退款商户到账金额
            merchantRefundCommand.setRefundPlatformProfit(platformProfit); //退款平台金利润
            merchantRefundCommand.setRefundMerchantProfit(merchantProfit);//代理商分润
            merchantRefundCommand.setRefundMerchantProfitList(merchantProfitList); //代理商分润明细
            merchantRefundCommand.setAccountOptType(AccountOptTypeEnum.REFUND);
            boolean merchantSettleRefund = settleAccountCmdService.handlerAccountToSettleRefund(merchantRefundCommand);
            log.info("doRefund4Pay tradeNo={} handlerAccountToSettleRefund result={}", tradeNo, merchantSettleRefund);

            //更新结算取消状态, 如果该订单正在结算,则有异常情况
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderService.update(settleUpdate);
            return;
        }

        //3、已结算、结算正在处理 - 则抛出异常，等会处理，等会要么成功，要么失败，!!! 当然也有小概率一直在处理中
        if (SettleStatusEnum.SETTLE_PROCESSING.equals(settleStatusEnum)) {
            throw new PaymentException("doRefund4Pay settle processing");
        }

        //4、已结算、结算成功，则进行退款
        if (SettleStatusEnum.SETTLE_SUCCESS.equals(settleStatusEnum)) {
            //商户退款 从可用金额中退款 退款金额该是商户的到账金额
            //平台退款 从可用金额中退款 退款金额该是平台利润金额
            BigDecimal accountAmount = settleOrder.getAccountAmount(); //退到账金额
            BigDecimal platformProfit = settleOrder.getPlatformProfit(); //退平台利润
            BigDecimal merchantProfit = settleOrder.getMerchantProfit();
            List<MerchantProfitDTO> merchantProfitList = Optional.of(settleOrder).map(SettleOrder::getAttribute)
                    .map(e -> JSONUtil.toBean(e, SettleAttributeDTO.class))
                    .map(SettleAttributeDTO::getMerchantProfitList).orElse(null);

            SettleAccountUpdateRefundCommand merchantRefundCommand = new SettleAccountUpdateRefundCommand();
            merchantRefundCommand.setTradeNo(settleOrder.getTradeNo());
            merchantRefundCommand.setOuterNo(settleOrder.getOuterNo());
            merchantRefundCommand.setMerchantId(settleOrder.getMerchantId());
            merchantRefundCommand.setMerchantName(settleOrder.getMerchantName());
            merchantRefundCommand.setAccountNo(settleOrder.getAccountNo());
            merchantRefundCommand.setCurrency(settleOrder.getCurrency());
            merchantRefundCommand.setRefundAccountAmount(accountAmount); //到账金额
            merchantRefundCommand.setRefundPlatformProfit(platformProfit); //平台利润
            merchantRefundCommand.setRefundMerchantProfit(merchantProfit);//代理商分润
            merchantRefundCommand.setRefundMerchantProfitList(merchantProfitList); //代理商分润明细
            merchantRefundCommand.setAccountOptType(AccountOptTypeEnum.REFUND);
            boolean merchantSettleRefund = settleAccountCmdService.handlerAccountSettleRefund(merchantRefundCommand);
            log.info("doRefund4Pay tradeNo={} handlerAccountSettleRefund result={}", tradeNo, merchantSettleRefund);

            //更新结算取消状态
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderService.update(settleUpdate);
        }

    }

    /**
     * 代付退款
     */
    private void doRefund4Cash(SettleRefundCmd command) {
        String tradeNo = command.getTradeNo();

        //查询结算
        QueryWrapper<SettleOrder> settleQuery = new QueryWrapper<>();
        settleQuery.lambda().eq(SettleOrder::getTradeNo, tradeNo).last(LIMIT_1);
        SettleOrder settleOrder = settleOrderService.getOne(settleQuery);

        //代付是同步的，能退款，肯定结算冻结已经执行过 - 较小概率
        if (Objects.isNull(settleOrder)) {
            saveCancelSettleOrder(command, TradeTypeEnum.PAYOUT);
            return;
        }

        //已结算 已结算失败， 解冻冻结金额
        SettleStatusEnum settleStatusEnum = SettleStatusEnum.codeToEnum(settleOrder.getSettleStatus());
        log.info("doRefund4Cash tradeNo={} settleStatusEnum={}", tradeNo, settleStatusEnum);

        if (SETTLE_TODO.equals(settleStatusEnum) || SETTLE_FAILED.equals(settleStatusEnum)) {

            //商户解冻
            SettleAccountUpdateUnFrozenCmd unfrozenCommand = new SettleAccountUpdateUnFrozenCmd();
            unfrozenCommand.setTradeNo(settleOrder.getTradeNo());
            unfrozenCommand.setOuterNo(settleOrder.getOuterNo());
            settleAccountCmdService.handlerAccountUnFrozen(unfrozenCommand);

            //更新结算为取消
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderService.update(settleUpdate);
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
            List<MerchantProfitDTO> merchantProfitList = Optional.of(settleOrder).map(SettleOrder::getAttribute)
                    .map(e -> JSONUtil.toBean(e, SettleAttributeDTO.class))
                    .map(SettleAttributeDTO::getMerchantProfitList).orElse(null);

            SettleAccountUpdateRefundCommand merchantRefundCommand = new SettleAccountUpdateRefundCommand();
            merchantRefundCommand.setTradeNo(settleOrder.getTradeNo());
            merchantRefundCommand.setOuterNo(settleOrder.getOuterNo());
            merchantRefundCommand.setMerchantId(settleOrder.getMerchantId());
            merchantRefundCommand.setMerchantName(settleOrder.getMerchantName());
            merchantRefundCommand.setAccountNo(settleOrder.getAccountNo());
            merchantRefundCommand.setCurrency(settleOrder.getCurrency());
            merchantRefundCommand.setRefundAccountAmount(merchantRefundAmount.negate());//取负数
            merchantRefundCommand.setRefundPlatformProfit(platformProfit);
            merchantRefundCommand.setRefundMerchantProfit(merchantProfit);//代理商分润
            merchantRefundCommand.setRefundMerchantProfitList(merchantProfitList); //代理商分润明细
            merchantRefundCommand.setAccountOptType(AccountOptTypeEnum.REFUND);
            boolean merchantSettleRefund = settleAccountCmdService.handlerAccountSettleRefund(merchantRefundCommand);
            log.info("doRefund4Cash tradeNo={} result={}", tradeNo, merchantSettleRefund);

            //结算状态更新为取消
            UpdateWrapper<SettleOrder> settleUpdate = new UpdateWrapper<>();
            settleUpdate.lambda().set(SettleOrder::getSettleStatus, SettleStatusEnum.SETTLE_CANCEL.getCode())
                    .eq(SettleOrder::getId, settleOrder.getId());
            settleOrderService.update(settleUpdate);
        }
    }

    /**
     * 保存取消的结算订单
     */
    private void saveCancelSettleOrder(SettleRefundCmd command, TradeTypeEnum tradeTypeEnum) {
        String tradeNo = command.getTradeNo();
        String alphanumeric4 = RandomStringUtils.randomAlphanumeric(4);
        String alphanumeric8 = RandomStringUtils.randomAlphanumeric(8);
        LocalDateTime now = LocalDateTime.now();

        SettleOrder settleOrder = new SettleOrder();
        settleOrder.setBusinessNo(IdWorker.getIdStr());
        settleOrder.setSettleNo(IdWorker.getIdStr());
        settleOrder.setSettleType(SettleTypeEnum.D0.name());
        settleOrder.setTradeNo(tradeNo);
        settleOrder.setTradeType(tradeTypeEnum.getCode());
        settleOrder.setTradeTime(now);
        settleOrder.setPaymentFinishTime(now);
        settleOrder.setMerchantId(alphanumeric8);
        settleOrder.setMerchantName(alphanumeric8);
        settleOrder.setAccountNo(alphanumeric8);
        settleOrder.setChannelCode(alphanumeric4);
        settleOrder.setChannelName(alphanumeric4);
        settleOrder.setPaymentMethod(alphanumeric4);
        settleOrder.setDeductionType(DeductionTypeEnum.DEDUCTION_INTERNAL.getCode());
        settleOrder.setCurrency(CurrencyEnum.IDR.name());
        settleOrder.setAccountAmount(BigDecimal.ZERO);
        settleOrder.setSettleStatus(SettleStatusEnum.SETTLE_CANCEL.getCode());
        settleOrder.setAttribute("{}");
        settleOrder.setCreateTime(LocalDateTime.now());
        settleOrderService.save(settleOrder);
    }

}
