package app.sphere.command.impl;

import app.sphere.manager.OrderNoManager;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import app.sphere.command.SettleAccountCmdService;
import app.sphere.command.cmd.SettleAccountAddCmd;
import app.sphere.command.cmd.SettleAccountRechargeCommand;
import app.sphere.command.cmd.SettleAccountUpdateCashCommand;
import app.sphere.command.cmd.SettleAccountUpdateCommand;
import app.sphere.command.cmd.SettleAccountUpdateFrozenCmd;
import app.sphere.command.cmd.SettleAccountUpdateRefundCommand;
import app.sphere.command.cmd.SettleAccountUpdateSettleCommand;
import app.sphere.command.cmd.SettleAccountUpdateTransferCommand;
import app.sphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import app.sphere.command.cmd.SettleAccountWithdrawCommand;
import app.sphere.command.dto.AccountDTO;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.SettleAccountFlow;
import share.sphere.enums.AccountOptTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.SettleAccountFlowRepository;
import domain.sphere.repository.SettleAccountRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static share.sphere.TradeConstant.LIMIT_1;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_PAYOUT;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_FEE;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_FROZEN;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_REFUND;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_WITHDRAW;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_INCOME_PAYMENT;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_INCOME_RECHARGE;
import static share.sphere.enums.AccountFlowDescEnum.MERCHANT_INCOME_UNFROZEN;
import static share.sphere.enums.AccountFlowDescEnum.PLATFORM_EXPEND_PAYOUT_CHANNEL;
import static share.sphere.enums.AccountFlowDescEnum.PLATFORM_EXPEND_PAYMENT_CHANNEL;
import static share.sphere.enums.AccountFlowDescEnum.PLATFORM_INCOME_PAYOUT_FEE;
import static share.sphere.enums.AccountFlowDescEnum.PLATFORM_INCOME_PAYMENT_FEE;
import static share.sphere.enums.AccountFlowDescEnum.PLATFORM_INCOME_WITHDRAW_FEE;

@Slf4j
@Service
public class SettleAccountCmdServiceImpl implements SettleAccountCmdService {

    // SQL常量定义
    private static final class SqlTemplate {
        static final String TO_SETTLE_ADD = "to_settle_balance = to_settle_balance + ";
        static final String TO_SETTLE_REDUCE = "to_settle_balance = to_settle_balance - ";
        static final String AVAILABLE_ADD = "available_balance = available_balance + ";
        static final String AVAILABLE_REDUCE = "available_balance = available_balance - ";
        static final String FROZEN_ADD = "frozen_balance = frozen_balance + ";
        static final String FROZEN_REDUCE = "frozen_balance = frozen_balance - ";
    }
    
    @Resource
    SettleAccountRepository accountService;
    @Resource
    SettleAccountFlowRepository accountFlowService;
    @Resource
    OrderNoManager orderNoManager;

    /**
     * 创建结算账户
     * @param command 创建命令，包含商户ID、商户名称、币种、地区、账户类型等信息
     * @return 创建是否成功
     * @throws PaymentException 当账户已存在时抛出异常
     */
    @Override
    public boolean addSettleAccount(SettleAccountAddCmd command) {
        log.info("[结算账户]开始创建账户, command={}", JSONUtil.toJsonStr(command));

        String accountNo = orderNoManager.getAccountNo(command.getCurrency(), command.getMerchantId());
        log.info("[结算账户]生成账户号成功, accountNo={}, merchantId={}, currency={}", 
            accountNo, command.getMerchantId(), command.getCurrency());

        //校验是否存在
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda()
            .eq(SettleAccount::getMerchantId, command.getMerchantId())
            .eq(SettleAccount::getAccountNo, accountNo)
            .last(LIMIT_1);
        SettleAccount account = accountService.getOne(accountQuery);
        if (account != null) {
            log.error("[结算账户]账户已存在, accountNo={}, merchantId={}, currency={}", 
                accountNo, command.getMerchantId(), command.getCurrency());
            throw new PaymentException(ExceptionCode.SETTLE_ACCOUNT_HAS_EXIST, accountNo);
        }
        log.info("[结算账户]账户校验通过, accountNo={} 不存在", accountNo);

        //新增账户
        account = new SettleAccount();
        account.setMerchantId(command.getMerchantId());
        account.setMerchantName(command.getMerchantName());
        account.setAccountType(command.getAccountType());
        account.setAccountNo(accountNo);
        account.setAccountName(command.getAccountName());
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setFrozenBalance(BigDecimal.ZERO);
        account.setToSettleBalance(BigDecimal.ZERO);
        account.setCurrency(command.getCurrency());
        account.setRegion(command.getRegion());
        account.setVersion(0);
        account.setAttribute("{}");
        account.setCreateTime(LocalDateTime.now());
        
        boolean result = accountService.save(account);
        log.info("账户创建完成, accountNo={}, result={}, account={}", accountNo, result, JSONUtil.toJsonStr(account));
        
        return result;
    }

    /**
     * 冻结账户资金
     * @param command 冻结命令，包含交易流水号、账户号、冻结金额等信息
     * @return 冻结是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlerAccountFrozen(SettleAccountUpdateFrozenCmd command) {
        log.info("[账户冻结]开始冻结, command={}", JSONUtil.toJsonStr(command));

        BigDecimal frozenAmount = command.getAmount();
        log.info("[账户冻结]冻结金额, amount={}", frozenAmount);

        //商户账户：
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.FROZEN_ADD + frozenAmount)
                .setSql(SqlTemplate.AVAILABLE_REDUCE + frozenAmount)
                .ge(SettleAccount::getAvailableBalance, frozenAmount)
                .eq(SettleAccount::getAccountNo, command.getAccountNo());
        boolean update = accountService.update(merchantAccountUpdate);
        if (!update) {
            log.error("[账户冻结]冻结失败, 余额不足, tradeNo={}, accountNo={}, amount={}", 
                command.getTradeNo(), command.getAccountNo(), frozenAmount);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Frozen failed. The balance is not enough");
        }
        log.info("[账户冻结]账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //增加资金流水
        SettleAccountFlow frozenFlow = new SettleAccountFlow();
        frozenFlow.setMerchantId(command.getMerchantId());
        frozenFlow.setMerchantName(command.getMerchantName());
        frozenFlow.setAccountNo(command.getAccountNo());
        frozenFlow.setAccountDirection(MERCHANT_EXPEND_FROZEN.getAccountDirection().getCode());
        frozenFlow.setAccountDirectionDesc(MERCHANT_EXPEND_FROZEN.name());
        frozenFlow.setCurrency(command.getCurrency());
        frozenFlow.setAmount(command.getAmount().negate());
        frozenFlow.setTradeNo(command.getTradeNo());
        frozenFlow.setFlowTime(System.currentTimeMillis());
        boolean save = accountFlowService.save(frozenFlow);
        if (!save) {
            log.error("[账户冻结]流水保存失败, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Frozen failed. accountFlow failed");
        }
        log.info("[账户冻结]流水保存成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
        
        return save;
    }

    /**
     * 解冻账户资金
     * @param command 解冻命令，包含交易流水号等信息
     * @return 解冻是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlerAccountUnFrozen(SettleAccountUpdateUnFrozenCmd command) {
        log.info("[账户解冻]开始解冻, command={}", JSONUtil.toJsonStr(command));

        BigDecimal unfrozenAmount = command.getAmount();
        log.info("[账户解冻]解冻金额, amount={}", unfrozenAmount);
        //商户账户：
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.FROZEN_REDUCE + unfrozenAmount)
                .setSql(SqlTemplate.AVAILABLE_ADD + unfrozenAmount)
                .ge(SettleAccount::getFrozenBalance, unfrozenAmount)
                .eq(SettleAccount::getAccountNo, command.getAccountNo());
        boolean update = accountService.update(merchantAccountUpdate);
        if (!update) {
            log.error("[账户解冻]账户更新失败, 冻结余额不足, tradeNo={}, accountNo={}, amount={}", 
                command.getTradeNo(), command.getAccountNo(), unfrozenAmount);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Unfrozen failed. balance is not enough");
        }
        log.info("[账户解冻]账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //增加资金流水
        SettleAccountFlow unfrozenFlow = new SettleAccountFlow();
        unfrozenFlow.setMerchantId(command.getMerchantId());
        unfrozenFlow.setMerchantName(command.getMerchantName());
        unfrozenFlow.setAccountNo(command.getAccountNo());
        unfrozenFlow.setAccountDirection(MERCHANT_INCOME_UNFROZEN.getAccountDirection().getCode());
        unfrozenFlow.setAccountDirectionDesc(MERCHANT_INCOME_UNFROZEN.name());
        unfrozenFlow.setCurrency(command.getCurrency());
        unfrozenFlow.setAmount(command.getAmount());
        unfrozenFlow.setTradeNo(command.getTradeNo());
        unfrozenFlow.setFlowTime(System.currentTimeMillis());
        boolean save = accountFlowService.save(unfrozenFlow);
        if (!save) {
            log.error("[账户解冻]流水保存失败, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Unfrozen failed. accountFlow failed");
        }
        log.info("[账户解冻]流水保存成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
        
        return save;
    }

    /**
     * 处理账户结算
     * @param command 结算命令，包含交易流水号、账户号、结算金额、结算类型等信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerAccountSettlement(SettleAccountUpdateSettleCommand command) {
        log.info("[账户结算]开始结算, command={}", JSONUtil.toJsonStr(command));

        //资金操作
        AccountOptTypeEnum accountOptTypeEnum = command.getAccountOptType();
        log.info("[账户结算]结算类型, tradeNo={}, type={}", command.getTradeNo(), accountOptTypeEnum.name());

        if (AccountOptTypeEnum.IMMEDIATE_SETTLE.equals(accountOptTypeEnum)) {
            immediateSettlement(command);
        } else if (AccountOptTypeEnum.PRE_SETTLE.equals(accountOptTypeEnum)) {
            preSettlement(command);
            return;
        } else if (AccountOptTypeEnum.DELAYED_SETTLE.equals(accountOptTypeEnum)) {
            delayedSettlement(command);
        }

        List<SettleAccountFlow> accountFlowList = new ArrayList<>();
        //商户收款流水
        SettleAccountFlow merchantIncomeFlow = new SettleAccountFlow();
        merchantIncomeFlow.setTradeNo(command.getTradeNo());
        merchantIncomeFlow.setMerchantId(command.getMerchantId());
        merchantIncomeFlow.setMerchantName(command.getMerchantName());
        merchantIncomeFlow.setAccountNo(command.getAccountNo());
        merchantIncomeFlow.setAccountDirection(MERCHANT_INCOME_PAYMENT.getAccountDirection().getCode());
        merchantIncomeFlow.setAccountDirectionDesc(MERCHANT_INCOME_PAYMENT.name());
        merchantIncomeFlow.setCurrency(command.getCurrency());
        merchantIncomeFlow.setAmount(command.getAmount());
        merchantIncomeFlow.setFlowTime(System.currentTimeMillis());
        merchantIncomeFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(merchantIncomeFlow);

        //商户手续费流水
        SettleAccountFlow merchantFeeFlow = new SettleAccountFlow();
        merchantFeeFlow.setTradeNo(command.getTradeNo());
        merchantFeeFlow.setMerchantId(command.getMerchantId());
        merchantFeeFlow.setMerchantName(command.getMerchantName());
        merchantFeeFlow.setAccountNo(command.getAccountNo());
        merchantFeeFlow.setAccountDirection(MERCHANT_EXPEND_FEE.getAccountDirection().getCode());
        merchantFeeFlow.setAccountDirectionDesc(MERCHANT_EXPEND_FEE.name());
        merchantFeeFlow.setCurrency(command.getCurrency());
        merchantFeeFlow.setAmount(command.getMerchantFee().negate());
        merchantFeeFlow.setFlowTime(System.currentTimeMillis());
        merchantFeeFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(merchantFeeFlow);

        //平台手续费收入流水
        SettleAccountFlow platformIncomeFlow = new SettleAccountFlow();
        platformIncomeFlow.setTradeNo(command.getTradeNo());
        platformIncomeFlow.setMerchantId(orderNoManager.getPlatformId());
        platformIncomeFlow.setMerchantName(orderNoManager.getPlatformName());
        platformIncomeFlow.setAccountNo(orderNoManager.getPlatformAccountNo(command.getCurrency()));
        platformIncomeFlow.setAccountDirection(PLATFORM_INCOME_PAYMENT_FEE.getAccountDirection().getCode());
        platformIncomeFlow.setAccountDirectionDesc(PLATFORM_INCOME_PAYMENT_FEE.name());
        platformIncomeFlow.setCurrency(command.getCurrency());
        platformIncomeFlow.setAmount(command.getAmount());
        platformIncomeFlow.setFlowTime(System.currentTimeMillis());
        platformIncomeFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(platformIncomeFlow);

        //平台渠道成本流水
        SettleAccountFlow platformChannelCostFlow = new SettleAccountFlow();
        platformChannelCostFlow.setTradeNo(command.getTradeNo());
        platformChannelCostFlow.setMerchantId(orderNoManager.getPlatformId());
        platformChannelCostFlow.setMerchantName(orderNoManager.getPlatformName());
        platformChannelCostFlow.setAccountNo(orderNoManager.getPlatformAccountNo(command.getCurrency()));
        platformChannelCostFlow.setAccountDirection(PLATFORM_EXPEND_PAYMENT_CHANNEL.getAccountDirection().getCode());
        platformChannelCostFlow.setAccountDirectionDesc(PLATFORM_EXPEND_PAYMENT_CHANNEL.name());
        platformChannelCostFlow.setCurrency(command.getCurrency());
        platformChannelCostFlow.setAmount(command.getChannelCost().negate());
        platformChannelCostFlow.setFlowTime(System.currentTimeMillis());
        platformChannelCostFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(platformChannelCostFlow);

        boolean saveBatch = accountFlowService.saveBatch(accountFlowList);
        if (!saveBatch) {
            log.error("[账户结算]流水保存失败, tradeNo={}", command.getTradeNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "AccountSettlement accountFlow failed");
        }
        log.info("[账户结算]流水保存成功, tradeNo={}, flowCount={}", command.getTradeNo(), accountFlowList.size());
    }

    /**
     * 处理账户出款
     * @param command 出款命令，包含交易流水号、账户号、出款金额、手续费等信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerAccountPayout(SettleAccountUpdateCashCommand command) {
        log.info("[账户出款]开始出款, command={}", JSONUtil.toJsonStr(command));

        String platformId = orderNoManager.getPlatformId();
        String platformName = orderNoManager.getPlatformName();
        String platformAccountNo = orderNoManager.getPlatformAccountNo(command.getCurrency());

        //商户账户：
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.FROZEN_REDUCE + command.getActualAmount())
                .ge(SettleAccount::getFrozenBalance, command.getActualAmount())
                .eq(SettleAccount::getAccountNo, command.getAccountNo());
        boolean update = accountService.update(merchantAccountUpdate);
        if (!update) {
            log.error("[账户出款]账户更新失败, 冻结余额不足, tradeNo={}, accountNo={}, amount={}", 
                command.getTradeNo(), command.getAccountNo(), command.getActualAmount());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Payout balance is not enough");
        }
        log.info("[账户出款]商户账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //平台账户：
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_ADD + command.getPlatformProfit())
                .eq(SettleAccount::getAccountNo, platformAccountNo);
        update = accountService.update(platformAccountUpdate);
        if (!update) {
            log.error("[账户出款]平台账户更新失败, tradeNo={}, accountNo={}",
                    command.getTradeNo(),
                    platformAccountNo);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Cash platform account failed");
        }
        log.info("[账户出款]平台账户更新成功, tradeNo={}, accountNo={}",
                command.getTradeNo(),
                platformAccountNo);

        List<SettleAccountFlow> accountFlowList = new ArrayList<>();
        //商户出款流水
        SettleAccountFlow merchantPayoutFlow = new SettleAccountFlow();
        merchantPayoutFlow.setTradeNo(command.getTradeNo());
        merchantPayoutFlow.setMerchantId(command.getMerchantId());
        merchantPayoutFlow.setMerchantName(command.getMerchantName());
        merchantPayoutFlow.setAccountNo(command.getAccountNo());
        merchantPayoutFlow.setAccountDirection(MERCHANT_EXPEND_PAYOUT.getAccountDirection().getCode());
        merchantPayoutFlow.setAccountDirectionDesc(MERCHANT_EXPEND_PAYOUT.name());
        merchantPayoutFlow.setCurrency(command.getCurrency());
        merchantPayoutFlow.setAmount(command.getActualAmount().negate());
        merchantPayoutFlow.setFlowTime(System.currentTimeMillis());
        merchantPayoutFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(merchantPayoutFlow);

        //平台手续费收入流水
        SettleAccountFlow platformIncomePayoutFeeFlow = new SettleAccountFlow();
        platformIncomePayoutFeeFlow.setTradeNo(command.getTradeNo());
        platformIncomePayoutFeeFlow.setMerchantId(platformId);
        platformIncomePayoutFeeFlow.setMerchantName(platformName);
        platformIncomePayoutFeeFlow.setAccountNo(platformAccountNo);
        platformIncomePayoutFeeFlow.setAccountDirection(PLATFORM_INCOME_PAYOUT_FEE.getAccountDirection().getCode());
        platformIncomePayoutFeeFlow.setAccountDirectionDesc(PLATFORM_INCOME_PAYOUT_FEE.name());
        platformIncomePayoutFeeFlow.setCurrency(command.getCurrency());
        platformIncomePayoutFeeFlow.setAmount(command.getMerchantFee());
        platformIncomePayoutFeeFlow.setFlowTime(System.currentTimeMillis());
        platformIncomePayoutFeeFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(platformIncomePayoutFeeFlow);

        //平台渠道成本流水
        SettleAccountFlow platformExpendPayoutChannelFlow = new SettleAccountFlow();
        platformExpendPayoutChannelFlow.setTradeNo(command.getTradeNo());
        platformExpendPayoutChannelFlow.setMerchantId(platformId);
        platformExpendPayoutChannelFlow.setMerchantName(platformName);
        platformExpendPayoutChannelFlow.setAccountNo(platformAccountNo);
        platformExpendPayoutChannelFlow.setAccountDirection(PLATFORM_EXPEND_PAYOUT_CHANNEL.getAccountDirection().getCode());
        platformExpendPayoutChannelFlow.setAccountDirectionDesc(PLATFORM_EXPEND_PAYOUT_CHANNEL.name());
        platformExpendPayoutChannelFlow.setCurrency(command.getCurrency());
        platformExpendPayoutChannelFlow.setAmount(command.getChannelCost().negate());
        platformExpendPayoutChannelFlow.setFlowTime(System.currentTimeMillis());
        platformExpendPayoutChannelFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(platformExpendPayoutChannelFlow);

        boolean saveBatch = accountFlowService.saveBatch(accountFlowList);
        if (!saveBatch) {
            log.error("[账户出款]流水保存失败, tradeNo={}", command.getTradeNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Cash accountFlow failed");
        }
        log.info("[账户出款]流水保存成功, tradeNo={}, flowCount={}", command.getTradeNo(), accountFlowList.size());
    }

    /**
     * 处理账户转账
     * @param command 转账命令，包含交易流水号、转出账户、转入账户、转账金额等信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlerAccountTransfer(SettleAccountUpdateTransferCommand command) {
        log.info("[账户转账]开始转账, command={}", JSONUtil.toJsonStr(command));

        // TODO: 实现转账逻辑
        log.warn("[账户转账]功能未实现, tradeNo={}", command.getTradeNo());
    }

    /**
     * 处理待结算退款
     *
     * @param command 退款命令，包含交易流水号、账户号、退款金额等信息
     */
    @Override
    public void handlerAccountToSettleRefund(SettleAccountUpdateRefundCommand command) {
        log.info("[账户退款]开始待结算退款, command={}", JSONUtil.toJsonStr(command));

        BigDecimal refundAccountAmount = command.getRefundAccountAmount();
        BigDecimal refundPlatformProfit = command.getRefundPlatformProfit();
        String platformAccountNo = orderNoManager.getPlatformAccountNo(command.getCurrency());

        //商户退款操作
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.TO_SETTLE_REDUCE + refundAccountAmount)
                .ge(SettleAccount::getToSettleBalance, refundAccountAmount)
                .eq(SettleAccount::getAccountNo, command.getAccountNo());
        boolean update = accountService.update(merchantAccountUpdate);
        if (!update) {
            log.error("[账户退款]商户账户更新失败, 待结算余额不足, tradeNo={}, accountNo={}, amount={}", 
                command.getTradeNo(), command.getAccountNo(), refundAccountAmount);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Refund error. Check balance is enough?");
        }
        log.info("[账户退款]商户账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //平台退款操作
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(SqlTemplate.TO_SETTLE_REDUCE + refundPlatformProfit)
                .ge(SettleAccount::getToSettleBalance, refundPlatformProfit)
                .eq(SettleAccount::getAccountNo, platformAccountNo);
        update = accountService.update(platformAccountUpdate);
        if (!update) {
            log.error("[账户退款]平台账户更新失败, 待结算余额不足, tradeNo={}, accountNo={}, amount={}", 
                command.getTradeNo(), platformAccountNo, refundPlatformProfit);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Refund error. Check balance is enough?");
        }
        log.info("[账户退款]平台账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), platformAccountNo);

    }

    /**
     * 处理已结算退款
     *
     * @param command 退款命令，包含交易流水号、账户号、退款金额等信息
     */
    @Override
    public void handlerAccountSettleRefund(SettleAccountUpdateRefundCommand command) {
        log.info("[账户退款]开始已结算退款, command={}", JSONUtil.toJsonStr(command));

        BigDecimal refundAccountAmount = command.getRefundAccountAmount();
        BigDecimal refundPlatformProfit = command.getRefundPlatformProfit();
        String platformId = orderNoManager.getPlatformId();
        String platformName = orderNoManager.getPlatformName();
        String platformAccount = orderNoManager.getPlatformAccountNo(command.getCurrency());

        //商户退款操作
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_REDUCE + refundAccountAmount)
                .ge(SettleAccount::getAvailableBalance, refundAccountAmount)
                .eq(SettleAccount::getAccountNo, command.getAccountNo());
        boolean update = accountService.update(merchantAccountUpdate);
        if (!update) {
            log.error("[账户退款]商户账户更新失败, 可用余额不足, tradeNo={}, accountNo={}, amount={}", 
                command.getTradeNo(), command.getAccountNo(), refundAccountAmount);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Refund error. Check balance is enough?");
        }
        log.info("[账户退款]商户账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //平台退款操作
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_REDUCE + refundPlatformProfit)
                .ge(SettleAccount::getAvailableBalance, refundPlatformProfit)
                .eq(SettleAccount::getAccountNo, platformAccount);
        update = accountService.update(platformAccountUpdate);
        if (!update) {
            log.error("[账户退款]平台账户更新失败, 可用余额不足, tradeNo={}, accountNo={}, amount={}",
                    command.getTradeNo(), platformAccount, refundPlatformProfit);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Refund error. Check balance is enough?");
        }
        log.info("[账户退款]平台账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), platformAccount);

        //插入流水
        List<SettleAccountFlow> accountFlowList = new ArrayList<>();

        //商户退款流水
        SettleAccountFlow merchantExpendRefundFlow = new SettleAccountFlow();
        merchantExpendRefundFlow.setTradeNo(command.getTradeNo());
        merchantExpendRefundFlow.setMerchantId(command.getMerchantId());
        merchantExpendRefundFlow.setMerchantName(command.getMerchantName());
        merchantExpendRefundFlow.setAccountNo(command.getAccountNo());
        merchantExpendRefundFlow.setAccountDirection(MERCHANT_EXPEND_REFUND.getAccountDirection().getCode());
        merchantExpendRefundFlow.setAccountDirectionDesc(MERCHANT_EXPEND_REFUND.name());
        merchantExpendRefundFlow.setCurrency(command.getCurrency());
        merchantExpendRefundFlow.setAmount(refundAccountAmount.negate());
        merchantExpendRefundFlow.setFlowTime(System.currentTimeMillis());
        merchantExpendRefundFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(merchantExpendRefundFlow);

        //平台退款流水
        SettleAccountFlow platformExpendRefundFlow = new SettleAccountFlow();
        platformExpendRefundFlow.setTradeNo(command.getTradeNo());
        platformExpendRefundFlow.setMerchantId(platformId);
        platformExpendRefundFlow.setMerchantName(platformName);
        platformExpendRefundFlow.setAccountNo(platformAccount);
        platformExpendRefundFlow.setAccountDirection(MERCHANT_EXPEND_REFUND.getAccountDirection().getCode());
        platformExpendRefundFlow.setAccountDirectionDesc(MERCHANT_EXPEND_REFUND.name());
        platformExpendRefundFlow.setCurrency(command.getCurrency());
        platformExpendRefundFlow.setAmount(refundPlatformProfit.negate());
        platformExpendRefundFlow.setFlowTime(System.currentTimeMillis());
        platformExpendRefundFlow.setCreateTime(LocalDateTime.now());
        accountFlowList.add(platformExpendRefundFlow);

        boolean save = accountFlowService.saveBatch(accountFlowList);
        if (!save) {
            log.error("[账户退款]流水保存失败, tradeNo={}", command.getTradeNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Refund accountFlow error");
        }
        log.info("[账户退款]流水保存成功, tradeNo={}, flowCount={}", command.getTradeNo(), accountFlowList.size());
    }

    /**
     * 处理账户充值
     * @param command 充值命令，包含交易流水号、账户号、充值金额等信息
     * @return 充值后的账户信息列表
     */
    @Override
    public void handlerAccountRecharge(SettleAccountRechargeCommand command) {
        log.info("[账户充值]开始充值, command={}", JSONUtil.toJsonStr(command));

        //商户充值操作
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_ADD + command.getAmount())
                .eq(SettleAccount::getAccountNo, command.getAccountNo());
        boolean update = accountService.update(merchantAccountUpdate);
        if (!update) {
            log.error("[账户充值]账户更新失败, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Recharge failed");
        }
        log.info("[账户充值]账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //插入充值流水
        SettleAccountFlow merchantRechargeFlow = new SettleAccountFlow();
        merchantRechargeFlow.setTradeNo(command.getTradeNo());
        merchantRechargeFlow.setMerchantId(command.getMerchantId());
        merchantRechargeFlow.setMerchantName(command.getMerchantName());
        merchantRechargeFlow.setAccountNo(command.getAccountNo());
        merchantRechargeFlow.setAccountDirection(MERCHANT_INCOME_RECHARGE.getAccountDirection().getCode());
        merchantRechargeFlow.setAccountDirectionDesc(MERCHANT_INCOME_RECHARGE.name());
        merchantRechargeFlow.setCurrency(command.getCurrency());
        merchantRechargeFlow.setAmount(command.getAmount());
        merchantRechargeFlow.setFlowTime(System.currentTimeMillis());
        merchantRechargeFlow.setCreateTime(LocalDateTime.now());
        boolean save = accountFlowService.save(merchantRechargeFlow);
        if (!save) {
            log.error("[账户充值]流水保存失败, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Recharge accountFlow failed");
        }
        log.info("[账户充值]流水保存成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
    }

    /**
     * 处理账户提现
     * @param command 提现命令，包含交易流水号、账户号、提现金额、手续费等信息
     * @return 提现后的账户信息列表
     */
    @Override
    public void handlerAccountWithdraw(SettleAccountWithdrawCommand command) {
        log.info("[账户提现]开始提现, command={}", JSONUtil.toJsonStr(command));

        String platformId = orderNoManager.getPlatformId();
        String platformName = orderNoManager.getPlatformName();
        String platformAccountNo = orderNoManager.getPlatformAccountNo(command.getCurrency());

        //商户提现操作
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.FROZEN_REDUCE + command.getAmount())
                .eq(SettleAccount::getAccountNo, command.getAccountNo());
        boolean update = accountService.update(merchantAccountUpdate);
        if (!update) {
            log.error("[账户提现]账户更新失败, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Account withdraw failed");
        }
        log.info("[账户提现]账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //平台操作
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_ADD + command.getMerchantFee())
                .eq(SettleAccount::getAccountNo, platformAccountNo);
        update = accountService.update(platformAccountUpdate);
        if (!update) {
            log.error("[账户提现]平台账户更新失败, tradeNo={}, accountNo={}", command.getTradeNo(), platformAccountNo);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Account withdraw error");
        }
        log.info("[账户提现]平台账户更新成功, tradeNo={}, accountNo={}", command.getTradeNo(), platformAccountNo);

        //商户提现流水
        SettleAccountFlow withdrawFlow = new SettleAccountFlow();
        withdrawFlow.setTradeNo(command.getTradeNo());
        withdrawFlow.setMerchantId(command.getMerchantId());
        withdrawFlow.setMerchantName(command.getMerchantName());
        withdrawFlow.setAccountNo(command.getAccountNo());
        withdrawFlow.setAccountDirection(MERCHANT_EXPEND_WITHDRAW.getAccountDirection().getCode());
        withdrawFlow.setAccountDirectionDesc(MERCHANT_EXPEND_WITHDRAW.name());
        withdrawFlow.setCurrency(command.getCurrency());
        withdrawFlow.setAmount(command.getAmount().negate());
        withdrawFlow.setFlowTime(System.currentTimeMillis());
        withdrawFlow.setCreateTime(LocalDateTime.now());
        boolean save = accountFlowService.save(withdrawFlow);
        if (!save) {
            log.error("[账户提现]流水保存失败, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Withdraw failed. accountFlow failed");
        }
        log.info("[账户提现]流水保存成功, tradeNo={}, accountNo={}", command.getTradeNo(), command.getAccountNo());

        //平台收入: 提现手续费
        SettleAccountFlow platformWithdrawFlow = new SettleAccountFlow();
        platformWithdrawFlow.setTradeNo(command.getTradeNo());
        platformWithdrawFlow.setMerchantId(platformId);
        platformWithdrawFlow.setMerchantName(platformName);
        platformWithdrawFlow.setAccountNo(platformAccountNo);
        platformWithdrawFlow.setAccountDirection(PLATFORM_INCOME_WITHDRAW_FEE.getAccountDirection().getCode());
        platformWithdrawFlow.setAccountDirectionDesc(PLATFORM_INCOME_WITHDRAW_FEE.name());
        platformWithdrawFlow.setCurrency(command.getCurrency());
        platformWithdrawFlow.setAmount(command.getMerchantFee());
        platformWithdrawFlow.setFlowTime(System.currentTimeMillis());
        platformWithdrawFlow.setCreateTime(LocalDateTime.now());
        save = accountFlowService.save(platformWithdrawFlow);
        if (!save) {
            log.error("[账户提现]平台流水保存失败, tradeNo={}, accountNo={}", command.getTradeNo(), platformAccountNo);
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "Withdraw failed. accountFlow failed");
        }
        log.info("[账户提现]平台流水保存成功, tradeNo={}, accountNo={}", command.getTradeNo(), platformAccountNo);
    }

    /**
     * 即可结算
     * 1、待结算金额减少
     * 2、可用金额增加
     * 注意：待结算金额大于等于交易金额 此观点就错误了，
     * 因为结算的时候， 平台利润可能是负数，则由于结算周期或早或晚，可能会是负数
     */
    private void immediateSettlement(SettleAccountUpdateCommand command) {
        log.info("immediateSettlement command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();
        //账户
        String merchantAccount = command.getAccountNo();
        String platformAccount = orderNoManager.getPlatformAccountNo(command.getCurrency());

        //商户账户： 可用余额 = 原来 + 到账金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_ADD + command.getAccountAmount())  //到账金额
                .eq(SettleAccount::getAccountNo, merchantAccount);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("immediateSettlement failed. TradeNo:" + tradeNo));

        //平台账户： 可用余额 = 原来 + 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_ADD + command.getPlatformProfit()) //平台利润
                .eq(SettleAccount::getAccountNo, platformAccount);
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("immediateSettlement failed. TradeNo:" + tradeNo));
    }

    /**
     * 延迟结算前置 操作账户
     * 1、加待结算金额
     * 商户到账金额 可能负数？ 可能但比较少
     * 平台利润 可能负数？ 及有可能，费率低，但渠道成本大
     */
    private void preSettlement(SettleAccountUpdateCommand command) {
        log.info("preSettlement command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();
        //账户
        String merchantAccount = command.getAccountNo();

        //商户账户： 待结算金额 = 原来 + 到账金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.TO_SETTLE_ADD + command.getAccountAmount()) //到账金额
                .eq(SettleAccount::getAccountNo, merchantAccount);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("preSettlement failed. TradeNo:" + tradeNo));
    }


    /**
     * 延迟结算
     * 1、待结算金额减少
     * 2、可用金额增加
     * 注意：待结算金额大于等于交易金额 此观点就错误了，
     * 因为结算的时候， 平台利润可能是负数，则由于结算周期或早或晚，可能会是负数
     */
    private void delayedSettlement(SettleAccountUpdateCommand command) {
        log.info("delayedSettlement command={}", JSONUtil.toJsonStr(command));
        String tradeNo = command.getTradeNo();
        // 账户
        String merchantAccount = command.getAccountNo();
        String platformAccount = orderNoManager.getPlatformAccountNo(command.getCurrency());

        // 商户账户：
        // 待结算余额 = 原来 - 到账金额；
        // 可用余额 = 原来 + 到账金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(SqlTemplate.TO_SETTLE_REDUCE + command.getAccountAmount()) //到账金额
                .setSql(SqlTemplate.AVAILABLE_ADD + command.getAccountAmount())
                .eq(SettleAccount::getAccountNo, merchantAccount);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("delayedSettlement failed. TradeNo:" + tradeNo));

        // 平台账户：
        // 可用余额 = 原来 + 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(SqlTemplate.AVAILABLE_ADD + command.getPlatformProfit())
                .eq(SettleAccount::getAccountNo, platformAccount);
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("delayedSettlement failed. TradeNo:" + tradeNo));
    }
}
