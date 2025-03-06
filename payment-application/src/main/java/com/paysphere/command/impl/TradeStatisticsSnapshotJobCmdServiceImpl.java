package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.command.TradeStatisticsSnapshotJobCmdService;
import com.paysphere.command.cmd.TradeStatisticsSnapshotJobCommand;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.db.entity.TradeSnapshotTradeAgentStatistics;
import com.paysphere.db.entity.TradeSnapshotTradeMerchantStatistics;
import com.paysphere.db.entity.TradeSnapshotTradeStatistics;
import com.paysphere.db.entity.TradeSnapshotTransferStatistics;
import com.paysphere.enums.CurrencyEnum;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.repository.TradePaymentOrderService;
import com.paysphere.repository.TradePayoutOrderService;
import com.paysphere.repository.TradeSnapshotTradeAgentStatisticsService;
import com.paysphere.repository.TradeSnapshotTradeMerchantStatisticsService;
import com.paysphere.repository.TradeSnapshotTradeStatisticsService;
import com.paysphere.repository.TradeSnapshotTransferStatisticsService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TradeStatisticsSnapshotJobCmdServiceImpl implements TradeStatisticsSnapshotJobCmdService {

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    TradePayoutOrderService tradePayoutOrderService;
    @Resource
    TradeSnapshotTradeStatisticsService tradeSnapshotTradeStatisticsService;
    @Resource
    TradeSnapshotTransferStatisticsService tradeSnapshotTransferStatisticsService;
    @Resource
    TradeSnapshotTradeAgentStatisticsService tradeSnapshotTradeAgentStatisticsService;
    @Resource
    TradeSnapshotTradeMerchantStatisticsService tradeSnapshotTradeMerchantStatisticsService;

    /**
     * 订单分析 - 订单快照
     */

    @Override
    @SneakyThrows
    public void handlerTradeStatisticsSnapshot(TradeStatisticsSnapshotJobCommand command) {
        log.info("handlerTradeStatisticsSnapshot command={}", JSONUtil.toJsonStr(command));

        // 查询商户基本信息
        /*MerchantListParam listParam = new MerchantListParam();
        listParam.setStatus(MerchantStatusEnum.NORMAL.getCode());
        List<MerchantBaseDTO> merchantList
                = BaseResult.parse(merchantApiService.getBaseMerchantList(listParam).toFuture().join(), false);
        if (CollectionUtils.isEmpty(merchantList)) {
            return;
        }*/
        List<Merchant> merchantList = new ArrayList<>();

        // 解析日期，默认D-1
        String tradeDate = Optional.ofNullable(command).map(TradeStatisticsSnapshotJobCommand::getTradeDate)
                .orElse(LocalDate.now().plusDays(-1).toString());
        String startTime = tradeDate + " 00:00:00";
        String endTime = tradeDate + " 23:59:59";
        log.info("handlerTradeStatisticsSnapshot startTime={}, endTime={}", startTime, endTime);

        // 如果已经存在某日的数据，先删除
        QueryWrapper<TradeSnapshotTradeStatistics> tradeDelete = new QueryWrapper<>();
        tradeDelete.lambda().eq(TradeSnapshotTradeStatistics::getTradeDate, tradeDate);
        tradeSnapshotTradeStatisticsService.remove(tradeDelete);
        QueryWrapper<TradeSnapshotTransferStatistics> transferDelete = new QueryWrapper<>();
        transferDelete.lambda().eq(TradeSnapshotTransferStatistics::getTradeDate, tradeDate);
        tradeSnapshotTransferStatisticsService.remove(transferDelete);
        QueryWrapper<TradeSnapshotTradeAgentStatistics> agentStatisticsDelete = new QueryWrapper<>();
        agentStatisticsDelete.lambda().eq(TradeSnapshotTradeAgentStatistics::getTradeDate, tradeDate);
        tradeSnapshotTradeAgentStatisticsService.remove(agentStatisticsDelete);
        QueryWrapper<TradeSnapshotTradeMerchantStatistics> merchantStatisticsDelete = new QueryWrapper<>();
        merchantStatisticsDelete.lambda().eq(TradeSnapshotTradeMerchantStatistics::getTradeDate, tradeDate);
        tradeSnapshotTradeMerchantStatisticsService.remove(merchantStatisticsDelete);
        log.info("handlerTradeStatisticsSnapshot remove old success");

        // 处理收款订单
        List<TradeSnapshotTradeStatistics> paySnapshotList = handlerPay(tradeDate, startTime, endTime, merchantList);
        log.info("handlerTradeStatisticsSnapshot paySnapshotList={}", JSONUtil.toJsonStr(paySnapshotList));
        tradeSnapshotTradeStatisticsService.saveBatch(paySnapshotList);

        // 处理代付订单
        List<TradeSnapshotTradeStatistics> cashSnapshotList = handlerCash(tradeDate, startTime, endTime, merchantList);
        log.info("handlerTradeStatisticsSnapshot cashSnapshotList={}", JSONUtil.toJsonStr(cashSnapshotList));
        tradeSnapshotTradeStatisticsService.saveBatch(cashSnapshotList);

        // 充值
        // 提现

        // 统计商商户 合并收款代付
        List<TradeSnapshotTradeMerchantStatistics> merchantStatisticsList = HandlerTradeMerchantStatistics(merchantList,
                tradeDate, paySnapshotList, cashSnapshotList);
        tradeSnapshotTradeMerchantStatisticsService.saveBatch(merchantStatisticsList);


    }


    // -----------------------------------


    /**
     * 统计商商户 合并收款代付
     */
    private List<TradeSnapshotTradeMerchantStatistics> HandlerTradeMerchantStatistics(List<Merchant> merchantList,
                                                                                      String tradeDate,
                                                                                      List<TradeSnapshotTradeStatistics> paySnapshotList,
                                                                                      List<TradeSnapshotTradeStatistics> cashSnapshotList) {
        return merchantList.stream().map(m -> {
            String merchantId = m.getMerchantId();
            int payCount = paySnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderCount)
                    .reduce(0, Integer::sum);

            int paySuccessCount = paySnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderSuccessCount)
                    .reduce(0, Integer::sum);

            BigDecimal payAmount = paySnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal paySuccessAmount = paySnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderSuccessAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int cashCount = cashSnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderCount)
                    .reduce(0, Integer::sum);

            int cashSuccessCount = cashSnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderSuccessCount)
                    .reduce(0, Integer::sum);

            BigDecimal cashAmount = cashSnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal cashSuccessAmount = cashSnapshotList.stream().filter(e -> e.getMerchantId().equals(merchantId))
                    .map(TradeSnapshotTradeStatistics::getOrderSuccessAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            TradeSnapshotTradeMerchantStatistics merchantStatistics = new TradeSnapshotTradeMerchantStatistics();
            merchantStatistics.setTradeDate(LocalDate.parse(tradeDate));
            merchantStatistics.setMerchantId(merchantId);
            merchantStatistics.setMerchantName(m.getMerchantName());
            merchantStatistics.setPayOrderCount(payCount);
            merchantStatistics.setPayOrderSuccessCount(paySuccessCount);
            merchantStatistics.setPayOrderAmount(payAmount);
            merchantStatistics.setPayOrderSuccessAmount(paySuccessAmount);
            merchantStatistics.setCashOrderCount(cashCount);
            merchantStatistics.setCashOrderSuccessCount(cashSuccessCount);
            merchantStatistics.setCashOrderAmount(cashAmount);
            merchantStatistics.setCashOrderSuccessAmount(cashSuccessAmount);
            merchantStatistics.setCreateTime(LocalDateTime.now());
            return merchantStatistics;
        }).toList();
    }


    /**
     * 处理代付订单
     */
    private List<TradeSnapshotTradeStatistics> handlerCash(String tradeDate, String startTime, String endTime,
                                                           List<Merchant> merchantList) {
        List<TradeSnapshotTradeStatistics> snapshotList = new ArrayList<>();
        // 查询代付订单
        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.select("merchant_id as merchantId, " +
                "merchant_name as merchantName, " +
                "payment_method as paymentMethod, " +
                "channel_code as channelCode, " +
                "payment_status as paymentStatus, " +
                "count(1) as version," +
                "sum(amount) as amount," +
                "sum(merchant_fee) as merchantFee," +
                "sum(account_amount) as accountAmount," +
                "sum(channel_cost) as channelCost," +
                "sum(platform_profit) as platformProfit"
        );
        cashOrderQuery.lambda().between(TradePayoutOrder::getTradeTime, startTime, endTime)
                .isNotNull(TradePayoutOrder::getPaymentMethod)
                .isNotNull(TradePayoutOrder::getChannelCode)
                .groupBy(TradePayoutOrder::getMerchantId)
                .groupBy(TradePayoutOrder::getPaymentMethod)
                .groupBy(TradePayoutOrder::getChannelCode)
                .groupBy(TradePayoutOrder::getPaymentStatus);
        List<TradePayoutOrder> cashOrderList = tradePayoutOrderService.list(cashOrderQuery);
        if (CollectionUtils.isEmpty(cashOrderList)) {
            log.warn("handlerCash no cash order.");
            return snapshotList;
        }

        // 分组
        Map<String, List<TradePayoutOrder>> cashOrderMap = cashOrderList.stream().collect(Collectors.groupingBy(e ->
                String.join(",", e.getMerchantId(), e.getPaymentMethod(), e.getChannelCode())));

        // 遍历
        for (Map.Entry<String, List<TradePayoutOrder>> entry : cashOrderMap.entrySet()) {
            String[] split = entry.getKey().split(",");
            String merchantId = split[0];
            String paymentMethod = split[1];
            String channelInfo = split[2]; // ChannelCode 包括code\Name

            String channelCode = channelInfo;
            String channelName = channelInfo;

            List<TradePayoutOrder> entryValue = entry.getValue();
            if (CollectionUtils.isEmpty(entryValue)) {
                continue;
            }

            // 代付订单数量
            Integer cashOrderCount = entryValue.stream()
                    .map(TradePayoutOrder::getVersion)
                    .reduce(Integer::sum)
                    .orElse(0);

            // 代付成功订单数量
            Integer cashOrderSuccessCount = entryValue.stream()
                    .filter(e -> e.getPaymentStatus().equals(PaymentStatusEnum.PAYMENT_SUCCESS.getCode()))
                    .map(TradePayoutOrder::getVersion)
                    .reduce(Integer::sum)
                    .orElse(0);

            // 代付订单金额
            BigDecimal cashOrderAmount = entryValue.stream()
                    .map(TradePayoutOrder::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 代付订单成功金额
            BigDecimal cashOrderSuccessAmount = entryValue.stream()
                    .filter(e -> e.getPaymentStatus().equals(PaymentStatusEnum.PAYMENT_SUCCESS.getCode()))
                    .map(TradePayoutOrder::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 商户手续费
            BigDecimal merchantFee = entryValue.stream().map(TradePayoutOrder::getMerchantFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 商户入账金额 到账金额
            BigDecimal accountAmount = entryValue.stream().map(TradePayoutOrder::getAccountAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 平台通道成本
            BigDecimal channelCost = entryValue.stream().map(TradePayoutOrder::getChannelCost)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            TradeSnapshotTradeStatistics snapshotCash = new TradeSnapshotTradeStatistics();
            snapshotCash.setTradeDate(LocalDate.parse(tradeDate));
            snapshotCash.setTradeType(TradeTypeEnum.PAYOUT.getCode());
            snapshotCash.setMerchantId(merchantId);
            snapshotCash.setPaymentMethod(paymentMethod);
            snapshotCash.setChannelCode(channelCode);
            snapshotCash.setChannelName(channelName);
            snapshotCash.setOrderCount(cashOrderCount);
            snapshotCash.setOrderSuccessCount(cashOrderSuccessCount);
            snapshotCash.setCurrency(CurrencyEnum.IDR.name());
            snapshotCash.setOrderAmount(cashOrderAmount);
            snapshotCash.setOrderSuccessAmount(cashOrderSuccessAmount);
            snapshotCash.setMerchantFee(merchantFee);
            snapshotCash.setAccountAmount(accountAmount);
            snapshotCash.setChannelCost(channelCost);
            snapshotCash.setCreateTime(LocalDateTime.now());
            snapshotList.add(snapshotCash);
        }

        return snapshotList;
    }


    /**
     * 处理收款订单
     * 查询支付方式和渠道编码都不为空， 空则表示未支付、或者支付失败
     * 按照商户ID、支付方式、渠道编码、支付状态进行分组
     */
    private List<TradeSnapshotTradeStatistics> handlerPay(String tradeDate, String startTime, String endTime,
                                                          List<Merchant> merchantList) {
        List<TradeSnapshotTradeStatistics> snapshotList = new ArrayList<>();

        // 查询收款订单
        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.select("merchant_id as merchantId, " +
                "merchant_name as merchantName, " +
                "payment_method as paymentMethod, " +
                "channel_code as channelCode, " +
                "payment_status as paymentStatus," +
                "count(1) as version," +
                "sum(amount) as amount," +
                "sum(merchant_fee) as merchantFee," +
                "sum(account_amount) as accountAmount," +
                "sum(channel_cost) as channelCost," +
                "sum(platform_profit) as platformProfit"
                );
        payOrderQuery.lambda().between(TradePaymentOrder::getTradeTime, startTime, endTime)
                .isNotNull(TradePaymentOrder::getPaymentMethod)
                .isNotNull(TradePaymentOrder::getChannelCode)
                .groupBy(TradePaymentOrder::getMerchantId)
                .groupBy(TradePaymentOrder::getPaymentMethod)
                .groupBy(TradePaymentOrder::getChannelCode)
                .groupBy(TradePaymentOrder::getPaymentStatus);
        List<TradePaymentOrder> payOrderList = tradePaymentOrderService.list(payOrderQuery);
        if (CollectionUtils.isEmpty(payOrderList)) {
            log.warn("handlerPay no pay order.");
            return snapshotList;
        }

        // 分组
        Map<String, List<TradePaymentOrder>> payOrderMap = payOrderList.stream().collect(Collectors.groupingBy(e ->
                String.join(",", e.getMerchantId(), e.getPaymentMethod(), e.getChannelCode())));

        // 遍历
        for (Map.Entry<String, List<TradePaymentOrder>> entry : payOrderMap.entrySet()) {
            String[] split = entry.getKey().split(",");
            String merchantId = split[0];
            String paymentMethod = split[1];
            String channelInfo = split[2]; // ChannelCode 包括code\Name
//            ChannelInfoDTO channelInfoDTO = getChannelInfoDTO(channelInfo);
//            String channelCode = channelInfoDTO.getChannelCode();
//            String channelName = channelInfoDTO.getChannelName();


            List<TradePaymentOrder> entryValue = entry.getValue();
            if (CollectionUtils.isEmpty(entryValue)) {
                continue;
            }

            // 收款订单数量
            Integer payOrderCount = entryValue.stream()
                    .map(TradePaymentOrder::getVersion)
                    .reduce(Integer::sum)
                    .orElse(0);

            // 收款成功订单数量
            Integer payOrderSuccessCount = entryValue.stream()
                    .filter(e -> e.getPaymentStatus().equals(PaymentStatusEnum.PAYMENT_SUCCESS.getCode()))
                    .map(TradePaymentOrder::getVersion)
                    .reduce(Integer::sum)
                    .orElse(0);

            // 收款订单金额
            BigDecimal payOrderAmount = entryValue.stream()
                    .map(TradePaymentOrder::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 收款订单成功金额
            BigDecimal payOrderSuccessAmount = entryValue.stream()
                    .filter(e -> e.getPaymentStatus().equals(PaymentStatusEnum.PAYMENT_SUCCESS.getCode()))
                    .map(TradePaymentOrder::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            log.info("handlerPay orderCount={}, payOrderSuccessCount={}, payOrderAmount={}, payOrderSuccessAmount={}",
                    payOrderCount, payOrderSuccessCount, payOrderAmount, payOrderSuccessAmount);

            // 商户手续费
            BigDecimal merchantFee = entryValue.stream().map(TradePaymentOrder::getMerchantFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 到账金额
            BigDecimal accountAmount = entryValue.stream().map(TradePaymentOrder::getAccountAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 渠道成本
            BigDecimal channelCost = entryValue.stream().map(TradePaymentOrder::getChannelCost)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            log.info("handlerPay merchantFee={}, accountAmount={}, channelCost={}, ",
                    merchantFee, accountAmount, channelCost);

            TradeSnapshotTradeStatistics snapshotPay = new TradeSnapshotTradeStatistics();
            snapshotPay.setTradeDate(LocalDate.parse(tradeDate));
            snapshotPay.setTradeType(TradeTypeEnum.PAYMENT.getCode());
            snapshotPay.setMerchantId(merchantId);
            snapshotPay.setMerchantName(null);
            snapshotPay.setPaymentMethod(paymentMethod);

            snapshotPay.setChannelCode(null);
            snapshotPay.setChannelName(null);
            snapshotPay.setOrderCount(payOrderCount);
            snapshotPay.setOrderSuccessCount(payOrderSuccessCount);
            snapshotPay.setCurrency(CurrencyEnum.IDR.name());
            snapshotPay.setOrderAmount(payOrderAmount);
            snapshotPay.setOrderSuccessAmount(payOrderSuccessAmount);
            snapshotPay.setMerchantFee(merchantFee);
            snapshotPay.setAccountAmount(accountAmount);
            snapshotPay.setChannelCost(channelCost);
            snapshotPay.setCreateTime(LocalDateTime.now());
            snapshotList.add(snapshotPay);
        }

        return snapshotList;
    }

}
