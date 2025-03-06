package com.paysphere.query.impl;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.db.entity.TradeSnapshotTradeAgentStatistics;
import com.paysphere.db.entity.TradeSnapshotTradeMerchantStatistics;
import com.paysphere.db.entity.TradeSnapshotTradeStatistics;
import com.paysphere.db.entity.TradeSnapshotTransferStatistics;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.query.SnapshotTradeStatisticsQueryService;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.TradeChannelDailySnapchatDTO;
import com.paysphere.query.dto.TradeMerchantDailySnapchatDTO;
import com.paysphere.query.dto.TradeMerchantStatisticsByAgentDTO;
import com.paysphere.query.dto.TradeMerchantStatisticsDTO;
import com.paysphere.query.dto.TradeMerchantStatisticsSnapshotDTO;
import com.paysphere.query.dto.TradePlatformDailySnapchatDTO;
import com.paysphere.query.dto.TradeStatisticsAgentDTO;
import com.paysphere.query.dto.TradeTimelyStatisticsIndexDTO;
import com.paysphere.query.dto.TradeTimelyStatisticsIndexSnapshotDTO;
import com.paysphere.query.dto.TransferDailySnapchatDTO;
import com.paysphere.query.param.TradeMerchantStatisticsSnapshotParam;
import com.paysphere.query.param.TradeStatisticsAgentPageParam;
import com.paysphere.query.param.TradeStatisticsByAgentPageParam;
import com.paysphere.query.param.TradeStatisticsChannelParam;
import com.paysphere.query.param.TradeStatisticsMerchantParam;
import com.paysphere.query.param.TradeStatisticsPlatformParam;
import com.paysphere.query.param.TradeStatisticsTransferParam;
import com.paysphere.query.param.TradeTimelyStatisticsIndexParam;
import com.paysphere.repository.TradePaymentOrderService;
import com.paysphere.repository.TradePayoutOrderService;
import com.paysphere.repository.TradeSnapshotTradeAgentStatisticsService;
import com.paysphere.repository.TradeSnapshotTradeMerchantStatisticsService;
import com.paysphere.repository.TradeSnapshotTradeStatisticsService;
import com.paysphere.repository.TradeSnapshotTransferStatisticsService;
import com.paysphere.utils.StorageUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SnapshotTradeStatisticsQueryServiceImpl implements SnapshotTradeStatisticsQueryService {

    @Resource
    TradeSnapshotTradeStatisticsService tradeSnapshotTradeStatisticsService;
    @Resource
    TradeSnapshotTransferStatisticsService tradeSnapshotTransferStatisticsService;
    @Resource
    TradeSnapshotTradeAgentStatisticsService tradeSnapshotTradeAgentStatisticsService;
    @Resource
    TradeSnapshotTradeMerchantStatisticsService tradeSnapshotTradeMerchantStatisticsService;
    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    TradePayoutOrderService tradePayoutOrderService;


    /**
     * 平台维度
     * 收款、代付维度
     */

    @Override
    public PageDTO<TradePlatformDailySnapchatDTO> getPlatformTradeStatistics(TradeStatisticsPlatformParam param) {
        log.info("getPlatformTradeStatistics param={}", JSONUtil.toJsonStr(param));

        // 根据日期分组 全部汇总 数量、金额
        QueryWrapper<TradeSnapshotTradeStatistics> snapshotQuery = new QueryWrapper<>();
        snapshotQuery.select("trade_date as tradeDate, " +
                "currency as currency, " +
                "sum(order_success_amount) as orderSuccessAmount, " +
                "sum(merchant_fee) as merchantFee, " +
                "sum(account_amount) as accountAmount, " +
                "sum(channel_cost) as channelCost, " +
                "sum(order_count) as orderCount, " +
                "sum(order_success_count) as orderSuccessCount");
        snapshotQuery.lambda().eq(TradeSnapshotTradeStatistics::getTradeType, param.getTradeType())
                .between(TradeSnapshotTradeStatistics::getTradeDate, param.getTradeStartDate(), param.getTradeEndDate())
                .groupBy(TradeSnapshotTradeStatistics::getTradeDate)
                .orderByDesc(TradeSnapshotTradeStatistics::getTradeDate);
        Page<TradeSnapshotTradeStatistics> page = tradeSnapshotTradeStatisticsService.page(new Page<>(param.getPageNum(),
                param.getPageSize()), snapshotQuery);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TradePlatformDailySnapchatDTO> collect = page.getRecords().stream().map(e -> {
            TradePlatformDailySnapchatDTO dto = new TradePlatformDailySnapchatDTO();

            BigDecimal successRate = new BigDecimal(e.getOrderSuccessCount())
                    .divide(new BigDecimal(e.getOrderCount()), 4, RoundingMode.HALF_UP);
            dto.setTradeDate(e.getTradeDate().toString());
            dto.setCurrency(e.getCurrency());
            dto.setOrderSuccessAmount(e.getOrderSuccessAmount());
            dto.setMerchantFee(e.getMerchantFee());
            dto.setAccountAmount(e.getAccountAmount());
            dto.setChannelCost(e.getChannelCost());
            dto.setOrderSuccessCount(e.getOrderSuccessCount());
            dto.setSuccessRate(successRate);
            return dto;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }


    @Override
    public String exportPlatformTradeStatistics(TradeStatisticsPlatformParam param) {
        log.info("exportPlatformTradeStatistics param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }
        param.setPageSize(1000);
        PageDTO<TradePlatformDailySnapchatDTO> platformTradeStatistics = getPlatformTradeStatistics(param);
        List<TradePlatformDailySnapchatDTO> snapchatDTOList = platformTradeStatistics.getData();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("trade-statistics-");
        String uploadObject = null;//storageHandler.uploadObject(snapchatDTOList, param.getOperator(), fileName,"PlatformTradeStatistics");
        log.info("exportPlatformTradeStatistics uploadObject={}", uploadObject);
        return uploadObject;
    }

    /**
     * 渠道、支付方式维度
     * 收款、代付维度
     */

    @Override
    public PageDTO<TradeChannelDailySnapchatDTO> getChannelTradeStatistics(TradeStatisticsChannelParam param) {
        log.info("getChannelTradeStatistics param={}", JSONUtil.toJsonStr(param));

        // 根据日期、支付方式、渠道分组 汇总数量、金额
        QueryWrapper<TradeSnapshotTradeStatistics> snapshotQuery = new QueryWrapper<>();
        snapshotQuery.select("trade_date as tradeDate, " +
                "payment_method as paymentMethod, " +
                "channel_code as channelCode, " +
                "channel_name as channelName, " +
                "currency as currency, " +
                "sum(order_success_amount) as orderSuccessAmount, " +
                "sum(merchant_fee) as merchantFee, " +
                "sum(account_amount) as accountAmount, " +
                "sum(channel_cost) as channelCost, " +
                "sum(order_count) as orderCount, " +
                "sum(order_success_count) as orderSuccessCount");
        snapshotQuery.lambda().eq(TradeSnapshotTradeStatistics::getTradeType, param.getTradeType())
                .between(TradeSnapshotTradeStatistics::getTradeDate, param.getTradeStartDate(), param.getTradeEndDate())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradeSnapshotTradeStatistics::getPaymentMethod,
                        param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelCode()), TradeSnapshotTradeStatistics::getChannelCode,
                        param.getChannelCode())
                .groupBy(TradeSnapshotTradeStatistics::getTradeDate)
                .groupBy(TradeSnapshotTradeStatistics::getPaymentMethod)
                .groupBy(TradeSnapshotTradeStatistics::getChannelCode)
                .orderByDesc(TradeSnapshotTradeStatistics::getTradeDate);
        Page<TradeSnapshotTradeStatistics> page = tradeSnapshotTradeStatisticsService.page(new Page<>(param.getPageNum(),
                param.getPageSize()), snapshotQuery);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TradeChannelDailySnapchatDTO> collect = page.getRecords().stream().map(e -> {
            TradeChannelDailySnapchatDTO dto = new TradeChannelDailySnapchatDTO();

            BigDecimal successRate = new BigDecimal(e.getOrderSuccessCount())
                    .divide(new BigDecimal(e.getOrderCount()), 4, RoundingMode.HALF_UP);
            dto.setTradeDate(e.getTradeDate().toString());
            dto.setPaymentMethod(e.getPaymentMethod());
            dto.setChannelCode(e.getChannelCode());
            dto.setChannelName(e.getChannelName());
            dto.setCurrency(e.getCurrency());
            dto.setOrderSuccessAmount(e.getOrderSuccessAmount());
            dto.setMerchantFee(e.getMerchantFee());
            dto.setAccountAmount(e.getAccountAmount());
            dto.setChannelCost(e.getChannelCost());
            dto.setOrderSuccessCount(e.getOrderSuccessCount());
            dto.setSuccessRate(successRate);
            return dto;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }


    @Override
    @SneakyThrows
    public String exportChannelTradeStatistics(TradeStatisticsChannelParam param) {
        log.info("exportChannelTradeStatistics param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }
        param.setPageSize(1000);
        PageDTO<TradeChannelDailySnapchatDTO> channelTradeStatistics = getChannelTradeStatistics(param);
        List<TradeChannelDailySnapchatDTO> snapchatDTOList = channelTradeStatistics.getData();

        StringWriter stringWriter = new StringWriter();
        CsvWriter csvWriter = CsvUtil.getWriter(stringWriter);

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("channel-trade-statistics-");
        String uploadObject = null;//storageHandler.uploadObject(snapchatDTOList, param.getOperator(), fileName, "ChannelTradeStatistics");
        log.info("exportChannelTradeStatistics uploadObject={}", uploadObject);
        return uploadObject;
    }


    /**
     * 商户维度
     * 收款、代付维度
     */

    @Override
    public PageDTO<TradeMerchantDailySnapchatDTO> getMerchantTradeStatistics(TradeStatisticsMerchantParam param) {
        log.info("getMerchantTradeStatistics param={}", JSONUtil.toJsonStr(param));

        // 根据日期、商户、支付方式、渠道分组 汇总金额、数量
        QueryWrapper<TradeSnapshotTradeStatistics> snapshotQuery = new QueryWrapper<>();
        snapshotQuery.select("trade_date as tradeDate, " +
                "merchant_id as merchantId, " +
                "merchant_name as merchantName, " +
                "payment_method as paymentMethod, " +
                "channel_code as channelCode, " +
                "channel_name as channelName, " +
                "currency as currency, " +
                "sum(order_success_amount) as orderSuccessAmount, " +
                "sum(merchant_fee) as merchantFee, " +
                "sum(account_amount) as accountAmount, " +
                "sum(channel_cost) as channelCost, " +
                "sum(order_count) as orderCount, " +
                "sum(order_success_count) as orderSuccessCount");
        snapshotQuery.lambda().eq(TradeSnapshotTradeStatistics::getTradeType, param.getTradeType())
                .between(TradeSnapshotTradeStatistics::getTradeDate, param.getTradeStartDate(), param.getTradeEndDate())
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradeSnapshotTradeStatistics::getPaymentMethod,
                        param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelCode()), TradeSnapshotTradeStatistics::getChannelCode,
                        param.getChannelCode())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeSnapshotTradeStatistics::getMerchantId,
                        param.getMerchantId())
                .groupBy(TradeSnapshotTradeStatistics::getTradeDate)
                .groupBy(TradeSnapshotTradeStatistics::getMerchantId)
                .groupBy(TradeSnapshotTradeStatistics::getPaymentMethod)
                .groupBy(TradeSnapshotTradeStatistics::getChannelCode)
                .orderByDesc(TradeSnapshotTradeStatistics::getTradeDate);
        Page<TradeSnapshotTradeStatistics> page = tradeSnapshotTradeStatisticsService.page(new Page<>(param.getPageNum(),
                param.getPageSize()), snapshotQuery);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TradeMerchantDailySnapchatDTO> collect = page.getRecords().stream().map(e -> {
            TradeMerchantDailySnapchatDTO dto = new TradeMerchantDailySnapchatDTO();

            BigDecimal successRate = new BigDecimal(e.getOrderSuccessCount())
                    .divide(new BigDecimal(e.getOrderCount()), 4, RoundingMode.HALF_UP);
            dto.setTradeDate(e.getTradeDate().toString());
            dto.setMerchantId(e.getMerchantId());
            dto.setMerchantName(e.getMerchantName());
            dto.setPaymentMethod(e.getPaymentMethod());
            dto.setChannelCode(e.getChannelCode());
            dto.setChannelName(e.getChannelName());
            dto.setCurrency(e.getCurrency());
            dto.setOrderSuccessAmount(e.getOrderSuccessAmount());
            dto.setMerchantFee(e.getMerchantFee());
            dto.setAccountAmount(e.getAccountAmount());
            dto.setChannelCost(e.getChannelCost());
            dto.setOrderSuccessCount(e.getOrderSuccessCount());
            dto.setSuccessRate(successRate);
            return dto;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }


    @Override
    @SneakyThrows
    public String exportMerchantTradeStatistics(TradeStatisticsMerchantParam param) {
        log.info("exportMerchantTradeStatistics param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }
        param.setPageSize(1000);
        PageDTO<TradeMerchantDailySnapchatDTO> merchantTradeStatistics = getMerchantTradeStatistics(param);
        List<TradeMerchantDailySnapchatDTO> snapchatDTOList = merchantTradeStatistics.getData();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("merchant-trade-statistics-");
        String uploadObject = null;//storageHandler.uploadObject(snapchatDTOList, param.getOperator(), fileName,"MerchantTradeStatistics");
        log.info("exportMerchantTradeStatistics uploadObject={}", uploadObject);
        return uploadObject;
    }

    /**
     * 平台、商户维度
     * 转账相关
     */

    @Override
    public PageDTO<TransferDailySnapchatDTO> getTransferStatistics(TradeStatisticsTransferParam param) {
        log.info("getPlatformTransferStatistics param={}", JSONUtil.toJsonStr(param));
        QueryWrapper<TradeSnapshotTransferStatistics> snapshotQuery = new QueryWrapper<>();
        snapshotQuery.lambda()
                .between(TradeSnapshotTransferStatistics::getTradeDate, param.getTradeStartDate(), param.getTradeEndDate())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeSnapshotTransferStatistics::getMerchantId,
                        param.getMerchantId())
                .in(CollectionUtils.isNotEmpty(param.getAccountTypeList()), TradeSnapshotTransferStatistics::getAccountType,
                        param.getAccountTypeList())
                .eq(Objects.nonNull(param.getTransferDirection()), TradeSnapshotTransferStatistics::getTransferDirection,
                        param.getTransferDirection())
                .orderByDesc(TradeSnapshotTransferStatistics::getTradeDate);
        Page<TradeSnapshotTransferStatistics> page = tradeSnapshotTransferStatisticsService.page(new Page<>(param.getPageNum(),
                param.getPageSize()), snapshotQuery);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TransferDailySnapchatDTO> collect = page.getRecords().stream().map(e -> {
            TransferDailySnapchatDTO snapchatDTO = new TransferDailySnapchatDTO();
            snapchatDTO.setTradeDate(e.getTradeDate().toString());
            snapchatDTO.setMerchantId(e.getMerchantId());
            snapchatDTO.setMerchantName(e.getMerchantName());
            snapchatDTO.setAccountNo(e.getAccountNo());
            snapchatDTO.setAccountType(e.getAccountType());
            snapchatDTO.setTransferDirection(e.getTransferDirection());
            snapchatDTO.setCurrency(e.getCurrency());
            snapchatDTO.setOrderSuccessAmount(e.getOrderSuccessAmount());
            return snapchatDTO;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }


    @Override
    @SneakyThrows
    public String exportTransferStatistics(TradeStatisticsTransferParam param) {
        log.info("exportTransferStatistics param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }
        param.setPageSize(1000);
        PageDTO<TransferDailySnapchatDTO> transferStatistics = getTransferStatistics(param);
        List<TransferDailySnapchatDTO> snapchatDTOList = transferStatistics.getData();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("transfer-statistics-");
        String uploadObject = null;//storageHandler.uploadObject(snapchatDTOList, param.getOperator(), fileName, "TransferStatistics");
        log.info("exportTransferStatistics uploadObject={}", uploadObject);
        return uploadObject;
    }

    /**
     * 商户维度
     * 收款、代付维度
     */

    @Override
    public TradeMerchantStatisticsDTO getMerchantTradeStatistics4Index(TradeMerchantStatisticsSnapshotParam param) {
        TradeMerchantStatisticsDTO statisticsDTO = new TradeMerchantStatisticsDTO();

        // 计算日期之间的日期，如果没有填充0
        LocalDate start = LocalDate.parse(param.getStartDate());
        LocalDate end = LocalDate.parse(param.getEndDate());
        List<String> dayList = new ArrayList<>();
        while (start.isBefore(end)) {
            dayList.add(start.toString());
            start = start.plusDays(1);
        }
        dayList.add(param.getEndDate());
        log.info("dayList={}", dayList);

        // 查询数据
        QueryWrapper<TradeSnapshotTradeStatistics> snapshotQuery = new QueryWrapper<>();
        snapshotQuery.select("trade_date as tradeDate, " +
                "merchant_id as merchantId, " +
                "merchant_name as merchantName, " +
                "trade_type as tradeType, " +
                "currency as currency, " +
                "sum(order_amount) as orderAmount, " +
                "sum(order_success_amount) as orderSuccessAmount, " +
                "sum(merchant_fee) as merchantFee, " +
                "sum(account_amount) as accountAmount, " +
                "sum(channel_cost) as channelCost, " +
                "sum(order_count) as orderCount, " +
                "sum(order_success_count) as orderSuccessCount");
        snapshotQuery.lambda()
                .between(TradeSnapshotTradeStatistics::getTradeDate, param.getStartDate(), param.getEndDate())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeSnapshotTradeStatistics::getMerchantId,
                        param.getMerchantId())
                .groupBy(TradeSnapshotTradeStatistics::getTradeDate)
                .orderByDesc(TradeSnapshotTradeStatistics::getTradeDate);
        List<TradeSnapshotTradeStatistics> statisticsList = tradeSnapshotTradeStatisticsService.list(snapshotQuery);


        if (CollectionUtils.isEmpty(statisticsList)) {
            List<TradeMerchantStatisticsSnapshotDTO> statisticsSnapshotDTOList = dayList.stream().map(e -> {
                TradeMerchantStatisticsSnapshotDTO snapshotDTO = new TradeMerchantStatisticsSnapshotDTO();
                snapshotDTO.setTradeDate(e);
                snapshotDTO.setMerchantId(param.getMerchantId());
                snapshotDTO.setMerchantName("");
                snapshotDTO.setPayAmount(BigDecimal.ZERO);
                snapshotDTO.setPaySuccessAmount(BigDecimal.ZERO);
                snapshotDTO.setPaySuccessRate(BigDecimal.ZERO);
                snapshotDTO.setCashAmount(BigDecimal.ZERO);
                snapshotDTO.setCashSuccessAmount(BigDecimal.ZERO);
                snapshotDTO.setCashSuccessRate(BigDecimal.ZERO);
                return snapshotDTO;
            }).toList();
            statisticsDTO.setSnapshotTradeStatisticsList(statisticsSnapshotDTOList);

        } else {
            TradeSnapshotTradeStatistics tradeStatistics = statisticsList.get(0);
            Set<LocalDate> tradeDateSet =
                    statisticsList.stream().map(TradeSnapshotTradeStatistics::getTradeDate).collect(Collectors.toSet());

            // 商户前几日的快照
            List<TradeMerchantStatisticsSnapshotDTO> collect = tradeDateSet.stream().map(e -> {
                // 收款
                BigDecimal payAmount = statisticsList.stream()
                        .filter(d -> d.getTradeType().equals(TradeTypeEnum.PAYMENT.getCode()))
                        .map(TradeSnapshotTradeStatistics::getOrderAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal paySuccessAmount = statisticsList.stream()
                        .filter(d -> d.getTradeType().equals(TradeTypeEnum.PAYMENT.getCode()))
                        .map(TradeSnapshotTradeStatistics::getOrderSuccessAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal payRate = payAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                        paySuccessAmount.divide(payAmount, 4,
                                RoundingMode.HALF_DOWN);

                // 收款
                BigDecimal cashAmount = statisticsList.stream()
                        .filter(d -> d.getTradeType().equals(TradeTypeEnum.PAYOUT.getCode()))
                        .map(TradeSnapshotTradeStatistics::getOrderAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal cashSuccessAmount = statisticsList.stream()
                        .filter(d -> d.getTradeType().equals(TradeTypeEnum.PAYOUT.getCode()))
                        .map(TradeSnapshotTradeStatistics::getOrderSuccessAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal cashRate = cashAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                        cashSuccessAmount.divide(cashAmount, 4,
                                RoundingMode.HALF_DOWN);

                TradeMerchantStatisticsSnapshotDTO snapshotDTO = new TradeMerchantStatisticsSnapshotDTO();
                snapshotDTO.setTradeDate(e.toString());
                snapshotDTO.setMerchantId(tradeStatistics.getMerchantId());
                snapshotDTO.setMerchantName(tradeStatistics.getMerchantName());
                snapshotDTO.setPayAmount(payAmount);
                snapshotDTO.setPaySuccessAmount(paySuccessAmount);
                snapshotDTO.setPaySuccessRate(payRate);
                snapshotDTO.setCashAmount(cashAmount);
                snapshotDTO.setCashSuccessAmount(cashSuccessAmount);
                snapshotDTO.setCashSuccessRate(cashRate);
                return snapshotDTO;
            }).toList();

            // 归纳
            List<TradeMerchantStatisticsSnapshotDTO> statisticsSnapshotDTOList = dayList.stream().map(e -> {
                TradeMerchantStatisticsSnapshotDTO statisticsSnapshotDTO =
                        collect.stream().filter(d -> d.getTradeDate().equals(e)).findAny().orElse(null);
                if (Objects.isNull(statisticsSnapshotDTO)) {
                    TradeMerchantStatisticsSnapshotDTO snapshotDTO = new TradeMerchantStatisticsSnapshotDTO();
                    snapshotDTO.setTradeDate(e);
                    snapshotDTO.setMerchantId(param.getMerchantId());
                    snapshotDTO.setMerchantName("");
                    snapshotDTO.setPayAmount(BigDecimal.ZERO);
                    snapshotDTO.setPaySuccessAmount(BigDecimal.ZERO);
                    snapshotDTO.setPaySuccessRate(BigDecimal.ZERO);
                    snapshotDTO.setCashAmount(BigDecimal.ZERO);
                    snapshotDTO.setCashSuccessAmount(BigDecimal.ZERO);
                    snapshotDTO.setCashSuccessRate(BigDecimal.ZERO);
                    return snapshotDTO;
                }
                return statisticsSnapshotDTO;
            }).toList();
            statisticsDTO.setSnapshotTradeStatisticsList(statisticsSnapshotDTOList);
        }

        // 是否包含今天的数据
        if (param.isIncludeToday()) {
            LocalDate today = LocalDate.now();
            String startTime = today + " 00:00:00";
            String endTime = today + " 23:59:59";
            log.info("today startTime={}, endTime={}", startTime, endTime);

            QueryWrapper<TradePaymentOrder> payQuery = new QueryWrapper<>();
            payQuery.select("sum(amount) as amount");
            payQuery.lambda().eq(TradePaymentOrder::getMerchantId, param.getMerchantId())
                    .between(TradePaymentOrder::getTradeTime, startTime, endTime)
                    .eq(TradePaymentOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode());
            TradePaymentOrder payOrder = tradePaymentOrderService.getOne(payQuery);
            Optional.ofNullable(payOrder).map(TradePaymentOrder::getAmount).ifPresent(statisticsDTO::setPayAmount);


            QueryWrapper<TradePayoutOrder> cashQuery = new QueryWrapper<>();
            cashQuery.select("sum(amount) as amount");
            cashQuery.lambda().eq(TradePayoutOrder::getMerchantId, param.getMerchantId())
                    .between(TradePayoutOrder::getTradeTime, startTime, endTime)
                    .eq(TradePayoutOrder::getPaymentStatus, PaymentStatusEnum.PAYMENT_SUCCESS.getCode());
            TradePayoutOrder cashOrder = tradePayoutOrderService.getOne(cashQuery);
            Optional.ofNullable(cashOrder).map(TradePayoutOrder::getAmount).ifPresent(statisticsDTO::setCashAmount);
        }

        return statisticsDTO;
    }


    @Override
    public TradeTimelyStatisticsIndexDTO getTradeTimelyStatistics4Index(TradeTimelyStatisticsIndexParam param) {
        TradeTimelyStatisticsIndexDTO statisticsDTO = new TradeTimelyStatisticsIndexDTO();
        String startTime = param.getStartTime();
        String endTime = param.getEndTime();
        log.info("getTradeTimelyStatistics4Index startTime={}, endTime={}", startTime, endTime);

        // 收款
        QueryWrapper<TradePaymentOrder> payQuery = new QueryWrapper<>();
        payQuery.select("payment_status as paymentStatus, sum(amount) as amount, count(1) as version");
        payQuery.lambda().between(TradePaymentOrder::getTradeTime, startTime, endTime)
                .groupBy(TradePaymentOrder::getPaymentStatus);
        List<TradePaymentOrder> payOrderList = tradePaymentOrderService.list(payQuery);
        log.info("getTradeTimelyStatistics4Index payOrderList={}", JSONUtil.toJsonStr(payOrderList));

        if (CollectionUtils.isNotEmpty(payOrderList)) {
            int payCount = 0;
            int paySuccessCount = 0;
            BigDecimal payAmount = BigDecimal.ZERO;
            BigDecimal paySuccessAmount = BigDecimal.ZERO;

            for (TradePaymentOrder payOrder : payOrderList) {
                payCount = payCount + payOrder.getVersion();
                payAmount = payAmount.add(payOrder.getAmount());

                PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(payOrder.getPaymentStatus());
                if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
                    paySuccessCount = paySuccessCount + payOrder.getVersion();
                    paySuccessAmount = paySuccessAmount.add(payOrder.getAmount());
                }
            }
            statisticsDTO.setPayCount(payCount);
            statisticsDTO.setPaySuccessCount(paySuccessCount);
            statisticsDTO.setPayAmount(payAmount);
            statisticsDTO.setPaySuccessAmount(paySuccessAmount);

            if (0 == payCount) {
                statisticsDTO.setPaySuccessRate(BigDecimal.ZERO);
            } else {
                statisticsDTO.setPaySuccessRate(new BigDecimal(paySuccessCount).divide(new BigDecimal(payCount), 4,
                        RoundingMode.UP));
            }
        }

        // 代付
        QueryWrapper<TradePayoutOrder> cashQuery = new QueryWrapper<>();
        cashQuery.select("payment_status as paymentStatus, sum(amount) as amount, count(1) as version");
        cashQuery.lambda().between(TradePayoutOrder::getTradeTime, startTime, endTime)
                .groupBy(TradePayoutOrder::getPaymentStatus);
        List<TradePayoutOrder> cashOrderList = tradePayoutOrderService.list(cashQuery);
        log.info("getTradeTimelyStatistics4Index cashOrderList={}", JSONUtil.toJsonStr(cashOrderList));

        if (CollectionUtils.isNotEmpty(cashOrderList)) {
            int cashCount = 0;
            int cashSuccessCount = 0;
            BigDecimal cashAmount = BigDecimal.ZERO;
            BigDecimal cashSuccessAmount = BigDecimal.ZERO;

            for (TradePayoutOrder cashOrder : cashOrderList) {
                cashCount = cashCount + cashOrder.getVersion();
                cashAmount = cashAmount.add(cashOrder.getAmount());

                PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.codeToEnum(cashOrder.getPaymentStatus());
                if (paymentStatusEnum.equals(PaymentStatusEnum.PAYMENT_SUCCESS)) {
                    cashSuccessCount = cashSuccessCount + cashOrder.getVersion();
                    cashSuccessAmount = cashSuccessAmount.add(cashOrder.getAmount());
                }
            }
            statisticsDTO.setCashCount(cashCount);
            statisticsDTO.setCashSuccessCount(cashSuccessCount);
            statisticsDTO.setCashAmount(cashAmount);
            statisticsDTO.setCashSuccessAmount(cashSuccessAmount);

            if (0 == cashCount) {
                statisticsDTO.setPaySuccessRate(BigDecimal.ZERO);
            } else {
                statisticsDTO.setCashSuccessRate(new BigDecimal(cashSuccessCount).divide(new BigDecimal(cashCount), 4
                        , RoundingMode.UP));
            }
        }

        // 查询近7日数据
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        List<String> dayList = new ArrayList<>();
        while (start.isBefore(end)) {
            dayList.add(start.toString());
            start = start.plusDays(1);
        }
        log.info("getTradeTimelyStatistics4Index dayList={}", dayList);

        String startDate = dayList.get(0);
        String endDate = dayList.get(dayList.size() - 1);
        QueryWrapper<TradeSnapshotTradeStatistics> snapshotQuery = new QueryWrapper<>();
        snapshotQuery.select("trade_date as tradeDate, " +
                "currency as currency, " +
                "sum(order_amount) as orderAmount, " +
                "sum(order_success_amount) as orderSuccessAmount");
        snapshotQuery.lambda().between(TradeSnapshotTradeStatistics::getTradeDate, startDate, endDate)
                .groupBy(TradeSnapshotTradeStatistics::getTradeDate);
        List<TradeSnapshotTradeStatistics> statisticsList = tradeSnapshotTradeStatisticsService.list(snapshotQuery);
        log.info("getTradeTimelyStatistics4Index statisticsList={}", JSONUtil.toJsonStr(statisticsList));

        if (CollectionUtils.isNotEmpty(statisticsList)) {
            List<TradeTimelyStatisticsIndexSnapshotDTO> statisticsSnapshotDTOList = dayList.stream().map(e -> {
                TradeSnapshotTradeStatistics tradeSnapshotTradeStatistics = statisticsList.stream()
                        .filter(s -> Objects.nonNull(s.getTradeDate()) && s.getTradeDate().toString().equals(e))
                        .findAny()
                        .orElse(null);
                log.info("getTradeTimelyStatistics4Index snapshotTradeStatistics={}",
                        JSONUtil.toJsonStr(tradeSnapshotTradeStatistics));

                TradeTimelyStatisticsIndexSnapshotDTO snapshotDTO = new TradeTimelyStatisticsIndexSnapshotDTO();
                snapshotDTO.setTradeDate(e);
                if (Objects.isNull(tradeSnapshotTradeStatistics)) {
                    snapshotDTO.setAmount(BigDecimal.ZERO);
                } else {
                    snapshotDTO.setAmount(tradeSnapshotTradeStatistics.getOrderAmount());
                }
                return snapshotDTO;
            }).toList();
            statisticsDTO.setSnapshotTradeStatisticsList(statisticsSnapshotDTOList);
        }

        return statisticsDTO;
    }

    @Override
    public PageDTO<TradeStatisticsAgentDTO> pageAgentTradeStatistics(TradeStatisticsAgentPageParam param) {
        QueryWrapper<TradeSnapshotTradeAgentStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StringUtils.isNotBlank(param.getAgentParentId()),
                        TradeSnapshotTradeAgentStatistics::getAgentParentId, param.getAgentParentId())
                .between(TradeSnapshotTradeAgentStatistics::getTradeDate, param.getStartTradeDate(), param.getEndTradeDate())
                .orderByDesc(TradeSnapshotTradeAgentStatistics::getId);
        Page<TradeSnapshotTradeAgentStatistics> page = tradeSnapshotTradeAgentStatisticsService.page(new Page<>(param.getPageNum(),
                param.getPageSize()), queryWrapper);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TradeStatisticsAgentDTO> collect = page.getRecords().stream().map(e -> {
            TradeStatisticsAgentDTO dto = new TradeStatisticsAgentDTO();
            dto.setTradeDate(e.getTradeDate());
            dto.setPayOrderCount(e.getPayOrderCount());
            dto.setPayOrderSuccessCount(e.getPayOrderSuccessCount());
            dto.setPayOrderAmount(e.getPayOrderAmount());
            dto.setPayOrderSuccessAmount(e.getPayOrderSuccessAmount());
            dto.setPayOrderSuccessRate(getRate(dto.getPayOrderSuccessCount(), dto.getPayOrderCount()));
            dto.setCashOrderCount(e.getCashOrderCount());
            dto.setCashOrderSuccessCount(e.getCashOrderSuccessCount());
            dto.setCashOrderAmount(e.getCashOrderAmount());
            dto.setCashOrderSuccessAmount(e.getCashOrderSuccessAmount());
            dto.setCashOrderSuccessRate(getRate(dto.getCashOrderSuccessCount(), dto.getCashOrderCount()));
            return dto;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }

    @Override
    public String exportAgentTradeStatistics(TradeStatisticsAgentPageParam param) {
        log.info("exportAgentTradeStatistics param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }
        param.setPageSize(1000);
        PageDTO<TradeStatisticsAgentDTO> transferStatistics = pageAgentTradeStatistics(param);
        List<TradeStatisticsAgentDTO> statisticsData = transferStatistics.getData();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("statistics-partner-");
        String uploadObject = null;//storageHandler.uploadObject(statisticsData, param.getOperator(), fileName, "AgentTradeStatistics");
        log.info("exportAgentTradeStatistics uploadObject={}", uploadObject);
        return uploadObject;
    }

    @Override
    public PageDTO<TradeMerchantStatisticsByAgentDTO> pageMerchantTradeStatisticsByAgent(TradeStatisticsByAgentPageParam param) {
        QueryWrapper<TradeSnapshotTradeMerchantStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .between(TradeSnapshotTradeMerchantStatistics::getTradeDate, param.getStartTradeDate(), param.getEndTradeDate())
                .orderByDesc(TradeSnapshotTradeMerchantStatistics::getId);
        Page<TradeSnapshotTradeMerchantStatistics> page = tradeSnapshotTradeMerchantStatisticsService.page(new Page<>(param.getPageNum(),
                param.getPageSize()), queryWrapper);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TradeMerchantStatisticsByAgentDTO> collect = page.getRecords().stream().map(e -> {
            TradeMerchantStatisticsByAgentDTO dto = new TradeMerchantStatisticsByAgentDTO();
            dto.setTradeDate(e.getTradeDate());
            dto.setMerchantId(e.getMerchantId());
            dto.setMerchantName(e.getMerchantName());
            dto.setPayOrderCount(e.getPayOrderCount());
            dto.setPayOrderSuccessCount(e.getPayOrderSuccessCount());
            dto.setPayOrderAmount(e.getPayOrderAmount());
            dto.setPayOrderSuccessAmount(e.getPayOrderSuccessAmount());
            dto.setPayOrderSuccessRate(getRate(dto.getPayOrderSuccessCount(), dto.getPayOrderCount()));
            dto.setCashOrderCount(e.getCashOrderCount());
            dto.setCashOrderSuccessCount(e.getCashOrderSuccessCount());
            dto.setCashOrderAmount(e.getCashOrderAmount());
            dto.setCashOrderSuccessAmount(e.getCashOrderSuccessAmount());
            dto.setCashOrderSuccessRate(getRate(dto.getCashOrderSuccessCount(), dto.getCashOrderCount()));
            return dto;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }

    @Override
    public String exportMerchantTradeStatisticsByAgent(TradeStatisticsByAgentPageParam param) {
        log.info("exportMerchantTradeStatisticsByAgent param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }
        param.setPageSize(1000);
        PageDTO<TradeMerchantStatisticsByAgentDTO> transferStatistics = pageMerchantTradeStatisticsByAgent(param);
        List<TradeMerchantStatisticsByAgentDTO> statisticsData = transferStatistics.getData();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("statistics-partner-merchant-");
        String uploadObject = null;//storageHandler.uploadObject(statisticsData, param.getOperator(), fileName, "MerchantTradeStatisticsByAgent");
        log.info("exportAgentTradeStatistics uploadObject={}", uploadObject);
        return uploadObject;
    }


    // -------------------------------------------------------------------------------------------------------


    private BigDecimal getRate(Integer dividend, Integer divisor) {
        if (Objects.isNull(dividend) || Objects.isNull(divisor)) {
            return BigDecimal.ZERO;
        }

        if (0 == dividend || 0 == divisor) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(dividend).divide(BigDecimal.valueOf(divisor), 4, RoundingMode.UP);
    }

}
