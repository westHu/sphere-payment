package app.sphere.command.impl;

import app.sphere.command.TradeStatisticsSnapshotJobCmdService;
import app.sphere.command.cmd.TradeStatisticsSnapshotJobCommand;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import domain.sphere.repository.TradePaymentOrderRepository;
import domain.sphere.repository.TradePayoutOrderRepository;
import domain.sphere.repository.TradeSnapshotTradeStatisticsRepository;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import infrastructure.sphere.db.entity.TradeSnapshotTradeStatistics;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.CurrencyEnum;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.TradeTypeEnum;

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
    TradePaymentOrderRepository tradePaymentOrderRepository;
    @Resource
    TradePayoutOrderRepository tradePayoutOrderRepository;
    @Resource
    TradeSnapshotTradeStatisticsRepository tradeSnapshotTradeStatisticsRepository;


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
        tradeSnapshotTradeStatisticsRepository.remove(tradeDelete);
        log.info("handlerTradeStatisticsSnapshot remove old success");

        // 处理收款订单
        List<TradeSnapshotTradeStatistics> paySnapshotList = handlerPayment(tradeDate, startTime, endTime, merchantList);
        log.info("handlerTradeStatisticsSnapshot paySnapshotList={}", JSONUtil.toJsonStr(paySnapshotList));
        tradeSnapshotTradeStatisticsRepository.saveBatch(paySnapshotList);

        // 处理代付订单
        List<TradeSnapshotTradeStatistics> cashSnapshotList = handlerPayout(tradeDate, startTime, endTime, merchantList);
        log.info("handlerTradeStatisticsSnapshot cashSnapshotList={}", JSONUtil.toJsonStr(cashSnapshotList));
        tradeSnapshotTradeStatisticsRepository.saveBatch(cashSnapshotList);

        // 充值
        // 提现


    }


    // -----------------------------------


    /**
     * 处理代付订单
     */
    private List<TradeSnapshotTradeStatistics> handlerPayout(String tradeDate, String startTime, String endTime,
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
        List<TradePayoutOrder> cashOrderList = tradePayoutOrderRepository.list(cashOrderQuery);
        if (CollectionUtils.isEmpty(cashOrderList)) {
            log.warn("handlerPayout no cash order.");
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
    private List<TradeSnapshotTradeStatistics> handlerPayment(String tradeDate, String startTime, String endTime,
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
        List<TradePaymentOrder> payOrderList = tradePaymentOrderRepository.list(payOrderQuery);
        if (CollectionUtils.isEmpty(payOrderList)) {
            log.warn("handlerPayment no pay order.");
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
            log.info("handlerPayment orderCount={}, payOrderSuccessCount={}, payOrderAmount={}, payOrderSuccessAmount={}",
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
            log.info("handlerPayment merchantFee={}, accountAmount={}, channelCost={}, ",
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
