package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.paysphere.command.SettleAccountCmdService;
import com.paysphere.command.cmd.SettleAccountAddCmd;
import com.paysphere.command.cmd.SettleAccountRechargeCommand;
import com.paysphere.command.cmd.SettleAccountUpdateCashCommand;
import com.paysphere.command.cmd.SettleAccountUpdateCommand;
import com.paysphere.command.cmd.SettleAccountUpdateFrozenCmd;
import com.paysphere.command.cmd.SettleAccountUpdateRefundCommand;
import com.paysphere.command.cmd.SettleAccountUpdateSettleCommand;
import com.paysphere.command.cmd.SettleAccountUpdateTransferCommand;
import com.paysphere.command.cmd.SettleAccountUpdateUnFrozenCmd;
import com.paysphere.command.cmd.SettleAccountWithdrawCommand;
import com.paysphere.command.dto.AccountDTO;
import com.paysphere.command.dto.AccountRecordAttributeDTO;
import com.paysphere.db.entity.SettleAccount;
import com.paysphere.db.entity.SettleAccountFlow;
import com.paysphere.enums.AccountOptTypeEnum;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.repository.SettleAccountFlowService;
import com.paysphere.repository.SettleAccountService;
import com.paysphere.utils.AccountUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.paysphere.TradeConstant.LIMIT_1;
import static com.paysphere.enums.AccountFlowDescEnum.EXPEND_TRANSFER;
import static com.paysphere.enums.AccountFlowDescEnum.INCOME_TRANSFER;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_CASH;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_FEE;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_FROZEN;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_REFUND;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_EXPEND_WITHDRAW;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_INCOME_PAY;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_INCOME_RECHARGE;
import static com.paysphere.enums.AccountFlowDescEnum.MERCHANT_INCOME_UNFROZEN;
import static com.paysphere.enums.AccountFlowDescEnum.PLATFORM_EXPEND_CASH_CHANNEL;
import static com.paysphere.enums.AccountFlowDescEnum.PLATFORM_EXPEND_PAY_CHANNEL;
import static com.paysphere.enums.AccountFlowDescEnum.PLATFORM_INCOME_CASH_FEE;
import static com.paysphere.enums.AccountFlowDescEnum.PLATFORM_INCOME_PAY_FEE;
import static com.paysphere.enums.AccountFlowDescEnum.PLATFORM_INCOME_WITHDRAW_FEE;

@Slf4j
@Service
public class SettleAccountCmdServiceImpl implements SettleAccountCmdService {

    public static final String TO_SETTLE_ADD_SQL = "to_settle_balance = to_settle_balance + ";
    public static final String TO_SETTLE_REDUCE_SQL = "to_settle_balance = to_settle_balance - ";
    public static final String AVAILABLE_ADD_SQL = "available_balance = available_balance + ";
    public static final String AVAILABLE_REDUCE_SQL = "available_balance = available_balance - ";
    public static final String FROZEN_ADD_SQL = "frozen_balance = frozen_balance + ";
    public static final String FROZEN_REDUCE_SQL = "frozen_balance = frozen_balance - ";

    @Resource
    SettleAccountService accountService;
    @Resource
    SettleAccountFlowService accountFlowService;
    @Resource
    RocketMqProducer rocketMqProducer;


    @Override
    public boolean addSettleAccount(SettleAccountAddCmd command) {
        log.info("addSettleAccount command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();
        String accountNo = command.getAccountNo();

        //校验是否存在
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda().eq(SettleAccount::getMerchantId, merchantId).eq(SettleAccount::getAccountNo, accountNo).last(LIMIT_1);
        SettleAccount account = accountService.getOne(accountQuery);
        Assert.isNull(account, () -> new PaymentException("account has exist. accountNo: " + accountNo));

        //新增
        account = new SettleAccount();
        account.setMerchantId(merchantId);
        account.setMerchantName(command.getMerchantName());
        account.setAccountType(command.getAccountType());
        account.setAccountNo(accountNo);
        account.setAccountName(command.getAccountName());
        account.setCurrentBalance(BigDecimal.ZERO);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setFrozenBalance(BigDecimal.ZERO);
        account.setToSettleBalance(BigDecimal.ZERO);
        account.setCurrency(command.getCurrency());
        account.setVersion(0);
        account.setAttribute("{}");
        account.setCreateTime(LocalDateTime.now());
        return accountService.save(account);
    }

    /**
     * 资金结算操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AccountDTO> handlerAccountSettlement(SettleAccountUpdateSettleCommand command) {
        log.info("handlerAccountSettlement command={}", JSONUtil.toJsonStr(command));
        LocalDateTime now = LocalDateTime.now();
        String tradeNo = command.getTradeNo();
        String batchNo = command.getBatchNo();
        String outerNo = command.getOuterNo();

        //商户
        String merchantId = command.getMerchantId();
        String merchantName = command.getMerchantName();
        String accountNo = command.getAccountNo();

        //平台
        String platformId = AccountUtil.getPlatformMerchantId();
        String platformName = AccountUtil.getPlatformMerchantName();
        String platformAccountNo = AccountUtil.getPlatformMerchantAccountNo();

        //资金
        String currency = command.getCurrency();
        BigDecimal amount = command.getAmount();
        BigDecimal merchantFee = command.getMerchantFee();
        BigDecimal accountAmount = command.getAccountAmount();
        BigDecimal platformProfit = command.getPlatformProfit();

        //代理商分润明细
        AccountRecordAttributeDTO attributeDTO = new AccountRecordAttributeDTO();
        attributeDTO.setMerchantProfitList(command.getMerchantProfitList());

        //资金操作
        AccountOptTypeEnum accountOptTypeEnum = command.getAccountOptType();
        log.info("updateAccount tradeNo={} accountOptTypeEnum={}", tradeNo, accountOptTypeEnum.name());

        List<AccountDTO> accountDTOList = new ArrayList<>();
        if (AccountOptTypeEnum.IMMEDIATE_SETTLE.equals(accountOptTypeEnum)) {
            accountDTOList = immediateSettlement(command);
        } else if (AccountOptTypeEnum.PRE_SETTLE.equals(accountOptTypeEnum)) {
            accountDTOList = preSettlement(command);
        } else if (AccountOptTypeEnum.DELAYED_SETTLE.equals(accountOptTypeEnum)) {
            accountDTOList = delayedSettlement(command);
        }


        //增加结算流水
        if (AccountOptTypeEnum.needSettlementFlow().contains(command.getAccountOptType())) {
            //增加流水
            List<SettleAccountFlow> accountFlowList = new ArrayList<>();

            //针对商户，+商户收款
            SettleAccountFlow merchantIncomeFlow = new SettleAccountFlow();
            merchantIncomeFlow.setAccountFlowNo(IdWorker.getIdStr());
            merchantIncomeFlow.setMerchantId(merchantId);
            merchantIncomeFlow.setMerchantName(merchantName);
            merchantIncomeFlow.setAccountNo(accountNo);
            merchantIncomeFlow.setAccountDirection(MERCHANT_INCOME_PAY.getAccountDirection().getCode());
            merchantIncomeFlow.setAccountDirectionDesc(MERCHANT_INCOME_PAY.name());
            merchantIncomeFlow.setCurrency(currency);
            merchantIncomeFlow.setAmount(amount);
            merchantIncomeFlow.setTradeNo(tradeNo);
            merchantIncomeFlow.setOuterNo(outerNo);
            merchantIncomeFlow.setFlowTime(now);
            merchantIncomeFlow.setCreateTime(now);
            accountFlowList.add(merchantIncomeFlow);

            //针对商户，-商户手续费
            SettleAccountFlow merchantFeeFlow = new SettleAccountFlow();
            merchantFeeFlow.setAccountFlowNo(IdWorker.getIdStr());
            merchantFeeFlow.setMerchantId(merchantId);
            merchantFeeFlow.setMerchantName(merchantName);
            merchantFeeFlow.setAccountNo(accountNo);
            merchantFeeFlow.setAccountDirection(MERCHANT_EXPEND_FEE.getAccountDirection().getCode());
            merchantFeeFlow.setAccountDirectionDesc(MERCHANT_EXPEND_FEE.name());
            merchantFeeFlow.setCurrency(currency);
            merchantFeeFlow.setAmount(merchantFee.negate());
            merchantFeeFlow.setTradeNo(tradeNo);
            merchantFeeFlow.setOuterNo(outerNo);
            merchantFeeFlow.setFlowTime(now);
            merchantFeeFlow.setCreateTime(now);
            accountFlowList.add(merchantFeeFlow);

            //针对平台 +商户手续费
            SettleAccountFlow platformIncomeFlow = new SettleAccountFlow();
            platformIncomeFlow.setAccountFlowNo(IdWorker.getIdStr());
            platformIncomeFlow.setMerchantId(platformId);
            platformIncomeFlow.setMerchantName(platformName);
            platformIncomeFlow.setAccountNo(platformAccountNo);
            platformIncomeFlow.setAccountDirection(PLATFORM_INCOME_PAY_FEE.getAccountDirection().getCode());
            platformIncomeFlow.setAccountDirectionDesc(PLATFORM_INCOME_PAY_FEE.name());
            platformIncomeFlow.setCurrency(currency);
            platformIncomeFlow.setAmount(merchantFee);
            platformIncomeFlow.setTradeNo(tradeNo);

            platformIncomeFlow.setOuterNo(outerNo);
            platformIncomeFlow.setFlowTime(now);
            platformIncomeFlow.setCreateTime(now);
            accountFlowList.add(platformIncomeFlow);

            //针对平台 -渠道成本
            SettleAccountFlow platformChannelCostFlow = new SettleAccountFlow();
            platformChannelCostFlow.setAccountFlowNo(IdWorker.getIdStr());
            platformChannelCostFlow.setMerchantId(platformId);
            platformChannelCostFlow.setMerchantName(platformName);
            platformChannelCostFlow.setAccountNo(platformAccountNo);
            platformChannelCostFlow.setAccountDirection(PLATFORM_EXPEND_PAY_CHANNEL.getAccountDirection().getCode());
            platformChannelCostFlow.setAccountDirectionDesc(PLATFORM_EXPEND_PAY_CHANNEL.name());
            platformChannelCostFlow.setCurrency(currency);
            platformChannelCostFlow.setAmount(command.getChannelCost().negate());
            platformChannelCostFlow.setTradeNo(tradeNo);

            platformChannelCostFlow.setOuterNo(outerNo);
            platformChannelCostFlow.setFlowTime(now);
            platformChannelCostFlow.setCreateTime(now);
            accountFlowList.add(platformChannelCostFlow);

            boolean saveBatch = accountFlowService.saveBatch(accountFlowList);
            Assert.isTrue(saveBatch, () -> new PaymentException("AccountSettlement accountFlow failed. TradeNo:" + tradeNo));
        }

        return accountDTOList;
    }

    /**
     * 资金冻结操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlerAccountFrozen(SettleAccountUpdateFrozenCmd command) {
        log.info("handlerAccountFrozen command={}", JSONUtil.toJsonStr(command));
        LocalDateTime now = LocalDateTime.now();

        //商户
        String tradeNo = command.getTradeNo();
        String outerNo = command.getOuterNo();
        String merchantId = command.getMerchantId();
        String merchantName = command.getMerchantName();
        String accountNo = command.getAccountNo();

        //平台
        String platformAccountNo = AccountUtil.getPlatformMerchantAccountNo();

        //冻结金额
        BigDecimal frozenAmount = command.getAmount();
        log.info("handlerAccountFrozen frozenAmount={}", frozenAmount);

        //商户账户：
        // 冻结余额 = 原来 + 冻结金额
        // 可用余额 = 原来- 冻结金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(FROZEN_ADD_SQL + frozenAmount)
                .setSql(AVAILABLE_REDUCE_SQL + frozenAmount)
                .ge(SettleAccount::getAvailableBalance, frozenAmount)
                .eq(SettleAccount::getAccountNo, accountNo);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Frozen failed. The balance is not enough. TradeNo: " + tradeNo));

        //增加资金流水 属于支出
        SettleAccountFlow frozenFlow = new SettleAccountFlow();
        frozenFlow.setAccountFlowNo(IdWorker.getIdStr());
        frozenFlow.setMerchantId(merchantId);
        frozenFlow.setMerchantName(merchantName);
        frozenFlow.setAccountNo(accountNo);
        frozenFlow.setAccountDirection(MERCHANT_EXPEND_FROZEN.getAccountDirection().getCode());
        frozenFlow.setAccountDirectionDesc(MERCHANT_EXPEND_FROZEN.name());
        frozenFlow.setCurrency(command.getCurrency());
        frozenFlow.setAmount(command.getAmount().negate()); //支出 为负数
        frozenFlow.setTradeNo(tradeNo);
        frozenFlow.setOuterNo(outerNo);
        frozenFlow.setFlowTime(now);
        frozenFlow.setCreateTime(now);
        boolean save = accountFlowService.save(frozenFlow);
        Assert.isTrue(save, () -> new PaymentException("Frozen failed. accountFlow failed. TradeNo:" + tradeNo));
        return save;
    }

    /**
     * 资金解冻操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlerAccountUnFrozen(SettleAccountUpdateUnFrozenCmd command) {
        log.info("handlerAccountUnFrozen command={}", JSONUtil.toJsonStr(command));

        LocalDateTime now = LocalDateTime.now();

        //单号
        String tradeNo = command.getTradeNo();
        String outerNo = command.getOuterNo();

        //平台
        String platformAccountNo = AccountUtil.getPlatformMerchantAccountNo();

        //查询冻结记录 或查询资金流水, 此处是冻结记录，考虑记录的数据量少
        QueryWrapper<SettleAccountFlow> recordQuery = new QueryWrapper<>();
        recordQuery.lambda().eq(SettleAccountFlow::getTradeNo, tradeNo)
                .last(LIMIT_1);
        SettleAccountFlow accountFlow = accountFlowService.getOne(recordQuery);
        Assert.notNull(accountFlow, () ->  new PaymentException("Unfrozen failed. Record not exist. TradeNo: " + tradeNo));
        log.info("handlerAccountUnFrozen accountFlow={}", JSONUtil.toJsonStr(accountFlow));

        //冻结相关, 解冻金额=冻结金额, 解冻账户=冻结账户
        String merchantId = accountFlow.getMerchantId();
        String merchantName = accountFlow.getMerchantName();
        String merchantAccountNo = accountFlow.getAccountNo();
        String currency = accountFlow.getCurrency();
        BigDecimal unFrozenAmount = accountFlow.getAmount();

        //商户账户：
        // 冻结余额 = 原来 - 解冻金额
        // 可用余额 = 原来 + 解冻金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(FROZEN_REDUCE_SQL + unFrozenAmount)
                .setSql(AVAILABLE_ADD_SQL + unFrozenAmount)
                .ge(SettleAccount::getFrozenBalance, unFrozenAmount) //此刻要添加，冻结金额不会是负数，所以解冻金额不会是负数
                .eq(SettleAccount::getAccountNo, merchantAccountNo);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Unfrozen failed. balance is not enough. TradeNo:" + tradeNo));

        //增加资金流水 属于收入
        SettleAccountFlow unfrozenFlow = new SettleAccountFlow();
        unfrozenFlow.setAccountFlowNo(IdWorker.getIdStr());
        unfrozenFlow.setMerchantId(merchantId);
        unfrozenFlow.setMerchantName(merchantName);
        unfrozenFlow.setAccountNo(merchantAccountNo);
        unfrozenFlow.setAccountDirection(MERCHANT_INCOME_UNFROZEN.getAccountDirection().getCode());
        unfrozenFlow.setAccountDirectionDesc(MERCHANT_INCOME_UNFROZEN.name());
        unfrozenFlow.setCurrency(currency);
        unfrozenFlow.setAmount(unFrozenAmount);
        unfrozenFlow.setTradeNo(tradeNo);
        unfrozenFlow.setOuterNo(outerNo);
        unfrozenFlow.setFlowTime(now);
        unfrozenFlow.setCreateTime(now);
        boolean save = accountFlowService.save(unfrozenFlow);
        Assert.isTrue(save, () -> new PaymentException("Unfrozen failed. accountFlow failed. TradeNo:" + tradeNo));
        return save;
    }

    /**
     * 资金出款操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AccountDTO> handlerAccountCash(SettleAccountUpdateCashCommand command) {
        log.info("handlerAccountCash command={}", JSONUtil.toJsonStr(command));

        LocalDateTime now = LocalDateTime.now();
        String tradeNo = command.getTradeNo();
        String outerNo = command.getOuterNo();

        //商户
        String merchantId = command.getMerchantId();
        String merchantName = command.getMerchantName();
        String accountNo = command.getAccountNo();

        //平台
        String platformId = AccountUtil.getPlatformMerchantId();
        String platformName = AccountUtil.getPlatformMerchantName();
        String platformAccountNo = AccountUtil.getPlatformMerchantAccountNo();

        //金额
        String currency = command.getCurrency();
        BigDecimal actualAmount = command.getActualAmount();
        BigDecimal merchantFee = command.getMerchantFee();
        BigDecimal channelCost = command.getChannelCost();
        BigDecimal platformProfit = command.getPlatformProfit();

        //商户账户：
        // 冻结金额 = 原来 - 出款金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(FROZEN_REDUCE_SQL + actualAmount)
                .ge(SettleAccount::getFrozenBalance, actualAmount) //冻结金额大于等于交易金额
                .eq(SettleAccount::getAccountNo, accountNo);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Cash balance is not enough. TradeNo:" + tradeNo));

        //平台账户：
        // 可用余额 = 原来 + 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(AVAILABLE_ADD_SQL + platformProfit)
                .eq(SettleAccount::getAccountNo, platformAccountNo);
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Cash platform account failed. TradeNo:" + tradeNo));

        List<SettleAccountFlow> accountFlowList = new ArrayList<>();

        //增加商户出款流水
        SettleAccountFlow merchantCashFlow = new SettleAccountFlow();
        merchantCashFlow.setAccountFlowNo(IdWorker.getIdStr());
        merchantCashFlow.setMerchantId(merchantId);
        merchantCashFlow.setMerchantName(merchantName);
        merchantCashFlow.setAccountNo(accountNo);
        merchantCashFlow.setAccountDirection(MERCHANT_EXPEND_CASH.getAccountDirection().getCode());
        merchantCashFlow.setAccountDirectionDesc(MERCHANT_EXPEND_CASH.name());
        merchantCashFlow.setCurrency(currency);
        merchantCashFlow.setAmount(actualAmount.negate()); //支出 为负数
        merchantCashFlow.setTradeNo(tradeNo);
        merchantCashFlow.setOuterNo(null);
        merchantCashFlow.setFlowTime(now);
        merchantCashFlow.setCreateTime(now);
        accountFlowList.add(merchantCashFlow);

        //平台收入:出款-商户手续费
        SettleAccountFlow platformIncomeCashFeeFlow = new SettleAccountFlow();
        platformIncomeCashFeeFlow.setAccountFlowNo(IdWorker.getIdStr());
        platformIncomeCashFeeFlow.setMerchantId(platformId);
        platformIncomeCashFeeFlow.setMerchantName(platformName);
        platformIncomeCashFeeFlow.setAccountNo(platformAccountNo);
        platformIncomeCashFeeFlow.setAccountDirection(PLATFORM_INCOME_CASH_FEE.getAccountDirection().getCode());
        platformIncomeCashFeeFlow.setAccountDirectionDesc(PLATFORM_INCOME_CASH_FEE.name());
        platformIncomeCashFeeFlow.setCurrency(currency);
        platformIncomeCashFeeFlow.setAmount(merchantFee);
        platformIncomeCashFeeFlow.setTradeNo(tradeNo);
        platformIncomeCashFeeFlow.setOuterNo(outerNo);
        platformIncomeCashFeeFlow.setFlowTime(now);
        platformIncomeCashFeeFlow.setCreateTime(now);
        accountFlowList.add(platformIncomeCashFeeFlow);

        //平台支出:出款-通道成本费用
        SettleAccountFlow platformExpendCashChannelFlow = new SettleAccountFlow();
        platformExpendCashChannelFlow.setAccountFlowNo(IdWorker.getIdStr());
        platformExpendCashChannelFlow.setMerchantId(platformId);
        platformExpendCashChannelFlow.setMerchantName(platformName);
        platformExpendCashChannelFlow.setAccountNo(platformAccountNo);
        platformExpendCashChannelFlow.setAccountDirection(PLATFORM_EXPEND_CASH_CHANNEL.getAccountDirection().getCode());
        platformExpendCashChannelFlow.setAccountDirectionDesc(PLATFORM_EXPEND_CASH_CHANNEL.name());
        platformExpendCashChannelFlow.setCurrency(currency);
        platformExpendCashChannelFlow.setAmount(channelCost.negate());
        platformExpendCashChannelFlow.setTradeNo(tradeNo);
        platformExpendCashChannelFlow.setOuterNo(outerNo);
        platformExpendCashChannelFlow.setFlowTime(now);
        platformExpendCashChannelFlow.setCreateTime(now);
        accountFlowList.add(platformExpendCashChannelFlow);

        boolean saveBatch = accountFlowService.saveBatch(accountFlowList);
        Assert.isTrue(saveBatch, () -> new PaymentException("Cash accountFlow failed. TradeNo:" + tradeNo));

        //再次查询余额
        AccountDTO accountDTO = getAccountDTO(accountNo);
        return Collections.singletonList(accountDTO);
    }

    /**
     * 资金转账操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AccountDTO> handlerAccountTransfer(SettleAccountUpdateTransferCommand command) {
        log.info("handlerAccountTransfer command={}", JSONUtil.toJsonStr(command));

        LocalDateTime now = LocalDateTime.now();
        String tradeNo = command.getTradeNo();

        // 转出商户
        String transferOutMerchantId = command.getMerchantId();
        String transferOutMerchantName = command.getMerchantName();
        String transferOutAccount = command.getAccountNo(); //转出账户

        // 转入商户
        String transferToMerchantId = command.getTransferToMerchantId();
        String transferToMerchantName = command.getTransferToMerchantName();
        String transferToAccount = command.getTransferToAccount(); //转入账户

        //转账金额
        String currency = command.getCurrency();
        BigDecimal transferAmount = command.getAmount();
        log.info("handlerAccountTransfer transferAmount={}", transferAmount);

        //转出账户
        // 冻结金额 = 原来 - 转账金额
        UpdateWrapper<SettleAccount> accountOutUpdate = new UpdateWrapper<>();
        accountOutUpdate.lambda()
                .setSql(FROZEN_REDUCE_SQL + transferAmount)
                .ge(SettleAccount::getFrozenBalance, transferAmount) //冻结资金大于等于交易金额
                .eq(SettleAccount::getAccountNo, transferOutAccount);
        boolean update = accountService.update(accountOutUpdate);
        Assert.isTrue(update, () -> new PaymentException("account out for transfer failed. TradeNo: " + tradeNo));

        //转入账户
        // 可用金额 = 原来 + 转账金额
        UpdateWrapper<SettleAccount> accountToUpdate = new UpdateWrapper<>();
        accountToUpdate.lambda()
                .setSql(AVAILABLE_ADD_SQL + transferAmount)
                .eq(SettleAccount::getAccountNo, transferToAccount);
        update = accountService.update(accountToUpdate);
        Assert.isTrue(update, () -> new PaymentException("account to for transfer failed. TradeNo: " + tradeNo));

        //流水
        //支出:资金转出
        SettleAccountFlow outcomeTransferFlow = new SettleAccountFlow();
        outcomeTransferFlow.setAccountFlowNo(IdWorker.getIdStr());
        outcomeTransferFlow.setMerchantId(transferOutMerchantId);
        outcomeTransferFlow.setMerchantName(transferOutMerchantName);
        outcomeTransferFlow.setAccountNo(transferOutAccount);
        outcomeTransferFlow.setAccountDirection(EXPEND_TRANSFER.getAccountDirection().getCode());
        outcomeTransferFlow.setAccountDirectionDesc(EXPEND_TRANSFER.name());
        outcomeTransferFlow.setCurrency(currency);
        outcomeTransferFlow.setAmount(transferAmount.negate()); //支出 为负数
        outcomeTransferFlow.setTradeNo(tradeNo);
        outcomeTransferFlow.setOuterNo(command.getOuterNo());
        outcomeTransferFlow.setFlowTime(now);
        outcomeTransferFlow.setCreateTime(now);
        boolean save = accountFlowService.save(outcomeTransferFlow);
        Assert.isTrue(save, () -> new PaymentException("Transfer failed. accountFlow failed. TradeNo:" + tradeNo));

        //收入:资金转入
        SettleAccountFlow incomeTransferFlow = new SettleAccountFlow();
        incomeTransferFlow.setAccountFlowNo(IdWorker.getIdStr());
        incomeTransferFlow.setMerchantId(transferToMerchantId);
        incomeTransferFlow.setMerchantName(transferToMerchantName);
        incomeTransferFlow.setAccountNo(transferToAccount);
        incomeTransferFlow.setAccountDirection(INCOME_TRANSFER.getAccountDirection().getCode());
        incomeTransferFlow.setAccountDirectionDesc(INCOME_TRANSFER.name());
        incomeTransferFlow.setCurrency(currency);
        incomeTransferFlow.setAmount(transferAmount);
        incomeTransferFlow.setTradeNo(tradeNo);
        incomeTransferFlow.setOuterNo(command.getOuterNo());
        incomeTransferFlow.setFlowTime(now);
        incomeTransferFlow.setCreateTime(now);
        save = accountFlowService.save(incomeTransferFlow);
        Assert.isTrue(save, () -> new PaymentException("Transfer failed. accountFlow failed. TradeNo:" + tradeNo));

        //再次查询余额
        return getAccountDTOList(transferOutAccount, transferToAccount);
    }

    /**
     * 资金退款操作 -待结算
     */
    @Override
    public boolean handlerAccountToSettleRefund(SettleAccountUpdateRefundCommand command) {
        log.info("handlerAccountToSettleRefund command={}", JSONUtil.toJsonStr(command));

        String tradeNo = command.getTradeNo();
        BigDecimal refundAccountAmount = command.getRefundAccountAmount();
        BigDecimal refundPlatformProfit = command.getRefundPlatformProfit();
        String platformAccount = AccountUtil.getPlatformMerchantAccountNo();
        String merchantAccountNo = command.getAccountNo();
        LocalDateTime now = LocalDateTime.now();

        //商户退款操作： 待结算金额 = 原来 - 退款金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(TO_SETTLE_REDUCE_SQL + refundAccountAmount) //到账金额
                .ge(SettleAccount::getToSettleBalance, refundAccountAmount) //待结算金额 大于等于 到账金额
                .eq(SettleAccount::getAccountNo, merchantAccountNo); //商户账户号
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Refund error. Check balance is enough? refer:" + tradeNo));

        //平台退款操作： 待结算金额 = 原来 - 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(TO_SETTLE_REDUCE_SQL + refundPlatformProfit) //平台利润
                .ge(SettleAccount::getToSettleBalance, refundPlatformProfit) //待结算金额 大于等于 平台利润
                .eq(SettleAccount::getAccountNo, platformAccount); //平台账户号
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Refund error. Check balance is enough? refer:" + tradeNo));

        return true;
    }

    /**
     * 资金退款操作 -已结算
     */
    @Override
    public boolean handlerAccountSettleRefund(SettleAccountUpdateRefundCommand command) {
        log.info("handlerAccountSettleRefund command={}", JSONUtil.toJsonStr(command));

        String tradeNo = command.getTradeNo();
        String outerNo = command.getOuterNo();
        BigDecimal refundAccountAmount = command.getRefundAccountAmount();
        BigDecimal refundPlatformProfit = command.getRefundPlatformProfit();
        LocalDateTime now = LocalDateTime.now();

        String platformId = AccountUtil.getPlatformMerchantId();
        String platformName = AccountUtil.getPlatformMerchantName();
        String platformAccount = AccountUtil.getPlatformMerchantAccountNo();

        //商户退款操作： 可用金额 = 原来 - 退款金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(AVAILABLE_REDUCE_SQL + refundAccountAmount) //到账金额
                .ge(SettleAccount::getAvailableBalance, refundAccountAmount) //可用余额 大于等于 到账金额
                .eq(SettleAccount::getAccountNo, command.getAccountNo()); //商户账户号
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Refund error. Check balance is enough? refer:" + tradeNo));

        //平台退款操作： 可用金额 = 原来 - 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(AVAILABLE_REDUCE_SQL + refundPlatformProfit) //平台利润
                .ge(SettleAccount::getAvailableBalance, refundPlatformProfit) //可用余额 大于等于 平台利润
                .eq(SettleAccount::getAccountNo, platformAccount); //平台账户号
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Refund error. Check balance is enough? refer:" + tradeNo));

        //插入流水 -> 3
        //商户支出:商户退款, 退到账金额
        List<SettleAccountFlow> accountFlowList = new ArrayList<>();
        SettleAccountFlow merchantExpendRefundFlow = new SettleAccountFlow();
        merchantExpendRefundFlow.setAccountFlowNo(IdWorker.getIdStr());
        merchantExpendRefundFlow.setMerchantId(command.getMerchantId());
        merchantExpendRefundFlow.setMerchantName(command.getMerchantName());
        merchantExpendRefundFlow.setAccountNo(command.getAccountNo());
        merchantExpendRefundFlow.setAccountDirection(MERCHANT_EXPEND_REFUND.getAccountDirection().getCode());
        merchantExpendRefundFlow.setAccountDirectionDesc(MERCHANT_EXPEND_REFUND.name());
        merchantExpendRefundFlow.setCurrency(command.getCurrency());
        merchantExpendRefundFlow.setAmount(refundAccountAmount.negate()); //退到账金额, 取负
        merchantExpendRefundFlow.setTradeNo(tradeNo);
        merchantExpendRefundFlow.setFlowTime(now);
        merchantExpendRefundFlow.setOuterNo(outerNo);
        merchantExpendRefundFlow.setCreateTime(now);
        accountFlowList.add(merchantExpendRefundFlow);

        //平台支出:商户退款, 退原来的利润
        SettleAccountFlow platformExpendRefundFlow = new SettleAccountFlow();
        platformExpendRefundFlow.setAccountFlowNo(IdWorker.getIdStr());
        platformExpendRefundFlow.setMerchantId(platformId);
        platformExpendRefundFlow.setMerchantName(platformName);
        platformExpendRefundFlow.setAccountNo(platformAccount);
        platformExpendRefundFlow.setAccountDirection(MERCHANT_EXPEND_REFUND.getAccountDirection().getCode());
        platformExpendRefundFlow.setAccountDirectionDesc(MERCHANT_EXPEND_REFUND.name());
        platformExpendRefundFlow.setCurrency(command.getCurrency());
        platformExpendRefundFlow.setAmount(refundPlatformProfit.negate()); //退到账金额, 取负
        platformExpendRefundFlow.setTradeNo(tradeNo);
        platformExpendRefundFlow.setFlowTime(now);
        platformExpendRefundFlow.setOuterNo(outerNo);
        platformExpendRefundFlow.setCreateTime(now);
        accountFlowList.add(platformExpendRefundFlow);
        boolean save = accountFlowService.saveBatch(accountFlowList);
        Assert.isTrue(save, () -> new PaymentException("Refund accountFlow error. refer:" + tradeNo));

        return save;
    }

    /**
     * 资金充值操作
     */
    @Override
    public List<AccountDTO> handlerAccountRecharge(SettleAccountRechargeCommand command) {
        log.info("handlerAccountRecharge command={}", JSONUtil.toJsonStr(command));

        LocalDateTime now = LocalDateTime.now();
        String tradeNo = command.getTradeNo();

        //商户
        String merchantId = command.getMerchantId();
        String merchantName = command.getMerchantName();
        String accountNo = command.getAccountNo();

        //平台
        String platformAccountNo = AccountUtil.getPlatformMerchantAccountNo();

        //商户充值操作：
        // 可用金额 = 原来 + 充值金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(AVAILABLE_ADD_SQL + command.getAmount())
                .eq(SettleAccount::getAccountNo, accountNo);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Recharge failed. TradeNo:" + tradeNo));

        //插入流水 充值只有一笔充值流水
        //商户收入:商户充值
        SettleAccountFlow merchantRechargeFlow = new SettleAccountFlow();
        merchantRechargeFlow.setAccountFlowNo(IdWorker.getIdStr());
        merchantRechargeFlow.setMerchantId(merchantId);
        merchantRechargeFlow.setMerchantName(merchantName);
        merchantRechargeFlow.setAccountNo(accountNo);
        merchantRechargeFlow.setAccountDirection(MERCHANT_INCOME_RECHARGE.getAccountDirection().getCode());
        merchantRechargeFlow.setAccountDirectionDesc(MERCHANT_INCOME_RECHARGE.name());
        merchantRechargeFlow.setCurrency(command.getCurrency());
        merchantRechargeFlow.setAmount(command.getAmount());
        merchantRechargeFlow.setTradeNo(tradeNo);
        merchantRechargeFlow.setFlowTime(now);
        merchantRechargeFlow.setOuterNo(null);
        merchantRechargeFlow.setCreateTime(now);
        boolean save = accountFlowService.save(merchantRechargeFlow);
        Assert.isTrue(save, () -> new PaymentException("Recharge accountFlow failed. TradeNo:" + tradeNo));

        //再次查询商户余额
        AccountDTO accountDTO = getAccountDTO(accountNo);
        return Collections.singletonList(accountDTO);
    }

    /**
     * 资金提现
     */
    @Override
    public List<AccountDTO> handlerAccountWithdraw(SettleAccountWithdrawCommand command) {
        log.info("handlerAccountWithdraw command={}", JSONUtil.toJsonStr(command));

        LocalDateTime now = LocalDateTime.now();
        String tradeNo = command.getTradeNo();

        //商户
        String merchantId = command.getMerchantId();
        String merchantName = command.getMerchantName();
        String accountNo = command.getAccountNo();

        //平台
        String platformId = AccountUtil.getPlatformMerchantId();
        String platformName = AccountUtil.getPlatformMerchantName();
        String platformAccountNo = AccountUtil.getPlatformMerchantAccountNo();

        //金额
        BigDecimal amount = command.getAmount();
        BigDecimal merchantFee = command.getMerchantFee();

        //商户提现操作：
        // 冻结余额 = 原来 - 提现金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(FROZEN_REDUCE_SQL + amount)
                .eq(SettleAccount::getAccountNo, accountNo);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("Account withdraw failed. TradeNo:" + tradeNo));

        //如果手续费不为0, 则平台加上提现手续费
        // 平台账户：可用余额 = 原来 + 提现手续费
        if (merchantFee.compareTo(BigDecimal.ZERO) != 0) {

            UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
            platformAccountUpdate.lambda()
                    .setSql(AVAILABLE_ADD_SQL + merchantFee)
                    .eq(SettleAccount::getAccountNo, platformAccountNo);
            update = accountService.update(platformAccountUpdate);
            Assert.isTrue(update, () -> new PaymentException("Account withdraw error. TradeNo:" + tradeNo));
        }

        //商户提现流水
        SettleAccountFlow withdrawFlow = new SettleAccountFlow();
        withdrawFlow.setAccountFlowNo(IdWorker.getIdStr());
        withdrawFlow.setMerchantId(merchantId);
        withdrawFlow.setMerchantName(merchantName);
        withdrawFlow.setAccountNo(accountNo);
        withdrawFlow.setAccountDirection(MERCHANT_EXPEND_WITHDRAW.getAccountDirection().getCode());
        withdrawFlow.setAccountDirectionDesc(MERCHANT_EXPEND_WITHDRAW.name());
        withdrawFlow.setCurrency(command.getCurrency());
        withdrawFlow.setAmount(command.getAmount().negate()); //支出 为负数
        withdrawFlow.setTradeNo(tradeNo);
        withdrawFlow.setOuterNo(null);
        withdrawFlow.setFlowTime(now);
        withdrawFlow.setCreateTime(now);
        boolean save = accountFlowService.save(withdrawFlow);
        Assert.isTrue(save, () -> new PaymentException("Withdraw failed. accountFlow failed. TradeNo:" + tradeNo));

        //平台收入: 提现手续费
        if (merchantFee.compareTo(BigDecimal.ZERO) != 0) {
            SettleAccountFlow platformWithdrawFlow = new SettleAccountFlow();
            platformWithdrawFlow.setAccountFlowNo(IdWorker.getIdStr());
            platformWithdrawFlow.setMerchantId(platformId);
            platformWithdrawFlow.setMerchantName(platformName);
            platformWithdrawFlow.setAccountNo(platformAccountNo);
            platformWithdrawFlow.setAccountDirection(PLATFORM_INCOME_WITHDRAW_FEE.getAccountDirection().getCode());
            platformWithdrawFlow.setAccountDirectionDesc(PLATFORM_INCOME_WITHDRAW_FEE.name());
            platformWithdrawFlow.setCurrency(command.getCurrency());
            platformWithdrawFlow.setAmount(merchantFee);
            platformWithdrawFlow.setTradeNo(tradeNo);
            platformWithdrawFlow.setFlowTime(now);
            platformWithdrawFlow.setOuterNo(null);
            platformWithdrawFlow.setCreateTime(now);
            save = accountFlowService.save(platformWithdrawFlow);
            Assert.isTrue(save, () -> new PaymentException("Withdraw failed. accountFlow failed. TradeNo:" + tradeNo));
        }

        //再次查询余额
        AccountDTO accountDTO = getAccountDTO(accountNo);
        return Collections.singletonList(accountDTO);
    }


    /**
     * 即可结算
     * 1、待结算金额减少
     * 2、可用金额增加
     * 注意：待结算金额大于等于交易金额 此观点就错误了，
     * 因为结算的时候， 平台利润可能是负数，则由于结算周期或早或晚，可能会是负数
     */
    private List<AccountDTO> immediateSettlement(SettleAccountUpdateCommand command) {
        log.info("immediateSettlement command={}", JSONUtil.toJsonStr(command));

        String tradeNo = command.getTradeNo();

        //账户
        String merchantAccount = command.getAccountNo();
        String platformAccount = AccountUtil.getPlatformMerchantAccountNo();

        //金额
        BigDecimal accountAmount = command.getAccountAmount();
        BigDecimal platformProfit = command.getPlatformProfit();

        //商户账户： 可用余额 = 原来 + 到账金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(AVAILABLE_ADD_SQL + accountAmount)  //到账金额
                .eq(SettleAccount::getAccountNo, merchantAccount);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("immediateSettlement failed. TradeNo:" + tradeNo));

        //平台账户： 可用余额 = 原来 + 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(AVAILABLE_ADD_SQL + platformProfit) //平台利润
                .eq(SettleAccount::getAccountNo, platformAccount);
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("immediateSettlement failed. TradeNo:" + tradeNo));

        //再次查询余额
        AccountDTO accountDTO = getAccountDTO(merchantAccount);
        return Collections.singletonList(accountDTO);
    }

    /**
     * 延迟结算前置 操作账户
     * 1、加待结算金额
     * 商户到账金额 可能负数？ 可能但比较少
     * 平台利润 可能负数？ 及有可能，费率低，但渠道成本大
     */
    private List<AccountDTO> preSettlement(SettleAccountUpdateCommand command) {
        log.info("preSettlement command={}", JSONUtil.toJsonStr(command));

        String tradeNo = command.getTradeNo();

        //账户
        String merchantAccount = command.getAccountNo();
        String platformAccount = AccountUtil.getPlatformMerchantAccountNo();

        //金额
        BigDecimal accountAmount = command.getAccountAmount();
        BigDecimal platformProfit = command.getPlatformProfit();

        //商户账户： 待结算金额 = 原来 + 到账金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(TO_SETTLE_ADD_SQL + accountAmount) //到账金额
                .eq(SettleAccount::getAccountNo, merchantAccount);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("preSettlement failed. TradeNo:" + tradeNo));

        //平台账户： 待结算金额 = 原来 + 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(TO_SETTLE_ADD_SQL + platformProfit) //平台利润
                .eq(SettleAccount::getAccountNo, platformAccount);
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("preSettlement failed. TradeNo:" + tradeNo));

        //再次查询余额
        AccountDTO accountDTO = getAccountDTO(command.getAccountNo());
        return Collections.singletonList(accountDTO);
    }


    /**
     * 延迟结算
     * 1、待结算金额减少
     * 2、可用金额增加
     * 注意：待结算金额大于等于交易金额 此观点就错误了，
     * 因为结算的时候， 平台利润可能是负数，则由于结算周期或早或晚，可能会是负数
     */
    private List<AccountDTO> delayedSettlement(SettleAccountUpdateCommand command) {
        log.info("delayedSettlement command={}", JSONUtil.toJsonStr(command));

        String tradeNo = command.getTradeNo();

        //账户
        String merchantAccount = command.getAccountNo();
        String platformAccount = AccountUtil.getPlatformMerchantAccountNo();

        //金额
        BigDecimal accountAmount = command.getAccountAmount();
        BigDecimal platformProfit = command.getPlatformProfit();

        //商户账户：
        // 待结算余额 = 原来 - 到账金额；
        // 可用余额 = 原来 + 到账金额
        UpdateWrapper<SettleAccount> merchantAccountUpdate = new UpdateWrapper<>();
        merchantAccountUpdate.lambda()
                .setSql(TO_SETTLE_REDUCE_SQL + accountAmount) //到账金额
                .setSql(AVAILABLE_ADD_SQL + accountAmount)
                .eq(SettleAccount::getAccountNo, merchantAccount);
        boolean update = accountService.update(merchantAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("delayedSettlement failed. TradeNo:" + tradeNo));

        //平台账户：
        // 待结算余额 = 原来 - 平台利润；
        // 可用余额 = 原来 + 平台利润
        UpdateWrapper<SettleAccount> platformAccountUpdate = new UpdateWrapper<>();
        platformAccountUpdate.lambda()
                .setSql(TO_SETTLE_REDUCE_SQL + platformProfit) //平台利润
                .setSql(AVAILABLE_ADD_SQL + platformProfit)
                .eq(SettleAccount::getAccountNo, platformAccount);
        update = accountService.update(platformAccountUpdate);
        Assert.isTrue(update, () -> new PaymentException("delayedSettlement failed. TradeNo:" + tradeNo));

        //再次查询余额
        AccountDTO accountDTO = getAccountDTO(merchantAccount);
        return Collections.singletonList(accountDTO);
    }

    /**
     * 查询商户此刻余额
     */
    private AccountDTO getAccountDTO(String merchantAccount) {
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda().eq(SettleAccount::getAccountNo, merchantAccount).last(LIMIT_1);
        SettleAccount account = accountService.getOne(accountQuery);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNo(account.getAccountNo());
        accountDTO.setAccountName(account.getAccountName());
        accountDTO.setCurrency(account.getCurrency());
        accountDTO.setAvailableBalance(account.getAvailableBalance());
        accountDTO.setFrozenBalance(account.getFrozenBalance());
        accountDTO.setToSettleBalance(account.getToSettleBalance());
        return accountDTO;
    }

    /**
     * 查询此刻余额
     */
    private List<AccountDTO> getAccountDTOList(String... accountNo) {
        List<String> accountNoList = Arrays.asList(accountNo);
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda().in(SettleAccount::getAccountNo, accountNoList);
        List<SettleAccount> accountList = accountService.list(accountQuery);

        return accountList.stream().map(account -> {
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setAccountNo(account.getAccountNo());
            accountDTO.setAccountName(account.getAccountName());
            accountDTO.setCurrency(account.getCurrency());
            accountDTO.setAvailableBalance(account.getAvailableBalance());
            accountDTO.setFrozenBalance(account.getFrozenBalance());
            accountDTO.setToSettleBalance(account.getToSettleBalance());
            return accountDTO;
        }).collect(Collectors.toList());
    }
}
