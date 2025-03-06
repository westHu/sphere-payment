package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.db.entity.SettleAccount;
import com.paysphere.db.entity.SettleAccountSnapshot;
import com.paysphere.db.entity.SettleOrder;
import com.paysphere.enums.AccountTypeEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.query.SettleStatisticsQueryService;
import com.paysphere.query.dto.SettleTimelyStatisticsIndexDTO;
import com.paysphere.query.param.SettleTimelyStatisticsIndexParam;
import com.paysphere.repository.SettleAccountService;
import com.paysphere.repository.SettleAccountSnapshotService;
import com.paysphere.repository.SettleOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class SettleStatisticsQueryServiceImpl implements SettleStatisticsQueryService {

    @Resource
    SettleOrderService settleOrderService;
    @Resource
    SettleAccountService accountService;
    @Resource
    SettleAccountSnapshotService accountSnapshotService;


    @Override
    public SettleTimelyStatisticsIndexDTO getSettleTimelyStatistics4Index(SettleTimelyStatisticsIndexParam param) {
        log.info("getSettleTimelyStatistics4Index param={}", JSONUtil.toJsonStr(param));

        SettleTimelyStatisticsIndexDTO statisticsDTO = new SettleTimelyStatisticsIndexDTO();

        //结算金额
        List<Integer> tradeTypeList = Stream.of(TradeTypeEnum.PAYMENT, TradeTypeEnum.PAYOUT)
                .map(TradeTypeEnum::getCode)
                .collect(Collectors.toList());
        QueryWrapper<SettleOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("trade_type as tradeType, " +
                "sum(merchant_fee) as merchantFee, " +
                "sum(merchant_profit) as merchantProfit, " +
                "sum(channel_cost) as channelCost, " +
                "sum(platform_profit) as platformProfit");
        queryWrapper.lambda().between(SettleOrder::getTradeTime, param.getStartTime(), param.getEndTime())
                .in(SettleOrder::getTradeType, tradeTypeList)
                .groupBy(SettleOrder::getTradeType);
        List<SettleOrder> settleOrderList = settleOrderService.list(queryWrapper);
        log.info("getSettleTimelyStatistics4Index settleOrderList={}", JSONUtil.toJsonStr(settleOrderList));

        if (CollectionUtils.isNotEmpty(settleOrderList)) {
            BigDecimal payMerchantFee = BigDecimal.ZERO;
            BigDecimal payMerchantProfit = BigDecimal.ZERO;
            BigDecimal payChannelCost = BigDecimal.ZERO;
            BigDecimal payPlatformProfit = BigDecimal.ZERO;

            BigDecimal cashMerchantFee = BigDecimal.ZERO;
            BigDecimal cashMerchantProfit = BigDecimal.ZERO;
            BigDecimal cashChannelCost = BigDecimal.ZERO;
            BigDecimal cashPlatformProfit = BigDecimal.ZERO;

            for (SettleOrder settleOrder : settleOrderList) {
                TradeTypeEnum tradeTypeEnum = TradeTypeEnum.codeToEnum(settleOrder.getTradeType());
                if (TradeTypeEnum.PAYMENT.equals(tradeTypeEnum)) {
                    payMerchantFee = payMerchantFee.add(settleOrder.getMerchantFee());
                    payMerchantProfit = payMerchantProfit.add(settleOrder.getMerchantProfit());
                    payChannelCost = payChannelCost.add(settleOrder.getChannelCost());
                    payPlatformProfit = payPlatformProfit.add(settleOrder.getPlatformProfit());
                } else if (TradeTypeEnum.PAYOUT.equals(tradeTypeEnum)) {
                    cashMerchantFee = cashMerchantFee.add(settleOrder.getMerchantFee());
                    cashMerchantProfit = cashMerchantProfit.add(settleOrder.getMerchantProfit());
                    cashChannelCost = cashChannelCost.add(settleOrder.getChannelCost());
                    cashPlatformProfit = cashPlatformProfit.add(settleOrder.getPlatformProfit());
                }
            }

            statisticsDTO.setPayMerchantFee(payMerchantFee);
            statisticsDTO.setPayMerchantProfit(payMerchantProfit);
            statisticsDTO.setPayChannelCost(payChannelCost);
            statisticsDTO.setPayPlatformProfit(payPlatformProfit);
            statisticsDTO.setCashMerchantFee(cashMerchantFee);
            statisticsDTO.setCashMerchantProfit(cashMerchantProfit);
            statisticsDTO.setCashChannelCost(cashChannelCost);
            statisticsDTO.setCashPlatformProfit(cashPlatformProfit);
        }

        String amountSql = "account_type as accountType, sum(available_balance) as availableBalance";
        //商户、商户资金-当前
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.select(amountSql);
        accountQuery.lambda().groupBy(SettleAccount::getAccountType);
        List<SettleAccount> accountList = accountService.list(accountQuery);
        log.info("getSettleTimelyStatistics4Index accountList={}", JSONUtil.toJsonStr(accountList));


        if (CollectionUtils.isNotEmpty(accountList)) {
            BigDecimal platformAmount = accountList.stream()
                    .filter(e -> AccountTypeEnum.codeToEnum(e.getAccountType()).equals(AccountTypeEnum.PLATFORM))
                    .map(SettleAccount::getAvailableBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal merchantAmount = accountList.stream()
                    .filter(e -> AccountTypeEnum.codeToEnum(e.getAccountType()).equals(AccountTypeEnum.MERCHANT_ACC))
                    .map(SettleAccount::getAvailableBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statisticsDTO.setPlatformAmount(platformAmount);
            statisticsDTO.setMerchantAmount(merchantAmount);

            /*for (Account account : accountList) {
                AccountTypeEnum accountTypeEnum = AccountTypeEnum.codeToEnum(account.getAccountType());
                if (AccountTypeEnum.PLATFORM.equals(accountTypeEnum)) {
                    statisticsDTO.setPlatformAmount(account.getAvailableBalance());
                } else if (AccountTypeEnum.MERCHANT_PAY_IN.equals(accountTypeEnum)) {
                    statisticsDTO.setMerchantAmount(account.getAvailableBalance());
                }
            }*/
        }

        //商户、商户资金-昨日新增
        String date1 = LocalDate.now().minusDays(1).toString();
        QueryWrapper<SettleAccountSnapshot> accountSnapshotQuery = new QueryWrapper<>();
        accountSnapshotQuery.select(amountSql);
        accountSnapshotQuery.lambda().eq(SettleAccountSnapshot::getAccountDate, date1)
                .groupBy(SettleAccountSnapshot::getAccountType);
        List<SettleAccountSnapshot> accountSnapshotList = accountSnapshotService.list(accountSnapshotQuery);
        log.info("getSettleTimelyStatistics4Index date1 accountSnapshotList={}",
                JSONUtil.toJsonStr(accountSnapshotList));

        if (CollectionUtils.isNotEmpty(accountSnapshotList)) {
            BigDecimal platformAmount1 = accountList.stream()
                    .filter(e -> AccountTypeEnum.codeToEnum(e.getAccountType()).equals(AccountTypeEnum.PLATFORM))
                    .map(SettleAccount::getAvailableBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal merchantAmount1 = accountList.stream()
                    .filter(e -> AccountTypeEnum.codeToEnum(e.getAccountType()).equals(AccountTypeEnum.MERCHANT_ACC))
                    .map(SettleAccount::getAvailableBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal platformAmount1Add = statisticsDTO.getPlatformAmount().subtract(platformAmount1);
            statisticsDTO.setPlatformAmount1Add(platformAmount1Add);
            BigDecimal merchantAmount1Add = statisticsDTO.getMerchantAmount().subtract(merchantAmount1);
            statisticsDTO.setMerchantAmount1Add(merchantAmount1Add);

            /*for (Account account : accountList) {
                AccountTypeEnum accountTypeEnum = AccountTypeEnum.codeToEnum(account.getAccountType());
                if (AccountTypeEnum.PLATFORM.equals(accountTypeEnum)) {
                    BigDecimal subtract = statisticsDTO.getPlatformAmount().subtract(account.getAvailableBalance());
                    statisticsDTO.setPlatformAmount1Add(subtract);
                } else if (AccountTypeEnum.MERCHANT_PAY_IN.equals(accountTypeEnum)) {
                    BigDecimal subtract = statisticsDTO.getMerchantAmount().subtract(account.getAvailableBalance());
                    statisticsDTO.setMerchantAmount1Add(subtract);
                }
            }*/
        }

        //商户、商户资金-7日新增
        String date7 = LocalDate.now().minusDays(7).toString();
        accountSnapshotQuery = new QueryWrapper<>();
        accountSnapshotQuery.select(amountSql);
        accountSnapshotQuery.lambda().eq(SettleAccountSnapshot::getAccountDate, date7)
                .groupBy(SettleAccountSnapshot::getAccountType);
        accountSnapshotList = accountSnapshotService.list(accountSnapshotQuery);
        log.info("getSettleTimelyStatistics4Index date7 accountSnapshotList={}",
                JSONUtil.toJsonStr(accountSnapshotList));

        if (CollectionUtils.isNotEmpty(accountSnapshotList)) {
            BigDecimal platformAmount7 = accountList.stream()
                    .filter(e -> AccountTypeEnum.codeToEnum(e.getAccountType()).equals(AccountTypeEnum.PLATFORM))
                    .map(SettleAccount::getAvailableBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal merchantAmount7 = accountList.stream()
                    .filter(e -> AccountTypeEnum.codeToEnum(e.getAccountType()).equals(AccountTypeEnum.MERCHANT_ACC))
                    .map(SettleAccount::getAvailableBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal platformAmount7Add = statisticsDTO.getPlatformAmount().subtract(platformAmount7);
            statisticsDTO.setPlatformAmount7Add(platformAmount7Add);
            BigDecimal merchantAmount7Add = statisticsDTO.getMerchantAmount().subtract(merchantAmount7);
            statisticsDTO.setMerchantAmount7Add(merchantAmount7Add);

            /*for (Account account : accountList) {
                AccountTypeEnum accountTypeEnum = AccountTypeEnum.codeToEnum(account.getAccountType());
                if (AccountTypeEnum.PLATFORM.equals(accountTypeEnum)) {
                    BigDecimal subtract = statisticsDTO.getPlatformAmount().subtract(account.getAvailableBalance());
                    statisticsDTO.setPlatformAmount7Add(subtract);
                } else if (AccountTypeEnum.MERCHANT_PAY_IN.equals(accountTypeEnum)) {
                    BigDecimal subtract = statisticsDTO.getMerchantAmount().subtract(account.getAvailableBalance());
                    statisticsDTO.setMerchantAmount7Add(subtract);
                }
            }*/
        }

        return statisticsDTO;
    }
}
