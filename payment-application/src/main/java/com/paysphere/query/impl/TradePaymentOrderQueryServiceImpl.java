package com.paysphere.query.impl;


import com.paysphere.assembler.ApplicationConverter;
import com.paysphere.cache.RedisService;
import com.paysphere.query.TradePaymentOrderQueryService;
import com.paysphere.repository.TradePaymentCallBackResultService;
import com.paysphere.repository.TradePaymentLinkOrderService;
import com.paysphere.repository.TradePaymentOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class TradePaymentOrderQueryServiceImpl
        implements TradePaymentOrderQueryService {

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    TradePaymentLinkOrderService tradePaymentLinkOrderService;

    @Resource
    ApplicationConverter applicationConverter;
    @Resource
    TradePaymentCallBackResultService tradePaymentCallBackResultService;
    @Resource
    RedisService redisService;



   /* @Override
    public Page<TradePaymentLinkOrder> pagePaymentLinkList(TradePaymentLinkPageParam param) {
        log.info("getPayOrderByTradeNo param={}", JSONUtil.toJsonStr(param));

        if (Objects.isNull(param)) {
            return new Page<>();
        }
        QueryWrapper<TradePaymentLinkOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(TradePaymentLinkOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getLinkNo()), TradePaymentLinkOrder::getLinkNo, param.getLinkNo())
                .eq(StringUtils.isNotBlank(param.getPaymentLink()), TradePaymentLinkOrder::getPaymentLink, param.getPaymentLink())
                .between(TradePaymentLinkOrder::getCreateTime, param.getCreateStartTime(), param.getCreateEndTime())
                .orderByDesc(TradePaymentLinkOrder::getId);
        return tradePaymentLinkOrderService.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
    }


    @Override
    public PageDTO<TradePayOrderPageDTO> pagePayOrderList(TradePayOrderPageParam param) {
        log.info("pagePayOrderList param={}", JSONUtil.toJsonStr(param));

        if (Objects.isNull(param)) {
            return PageDTO.empty();
        }
        if (Objects.nonNull(param.getAmountMin()) || Objects.nonNull(param.getAmountMax()) && 
                (StringUtils.isBlank(param.getMerchantId()))) {
                throw new PaymentException("If query by amount. Please select the merchant ID first.");
            
        }

        QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
        //单号索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradePaymentOrder::getTradeNo, param.getTradeNo())
                .in(CollectionUtils.isNotEmpty(param.getTradeNoList()), TradePaymentOrder::getTradeNo, param.getTradeNoList())
                .eq(StringUtils.isNotBlank(param.getOuterNo()), TradePaymentOrder::getOrderNo, param.getOuterNo())
                .in(CollectionUtils.isNotEmpty(param.getOuterNoList()), TradePaymentOrder::getOrderNo, param.getOuterNoList());
        //时间组合索引
        if (StringUtils.isNoneBlank(param.getTradeStartTime(), param.getTradeEndTime())) {
            queryWrapper.lambda().between(TradePaymentOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        if (StringUtils.isNoneBlank(param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime())) {
            queryWrapper.lambda().between(TradePaymentOrder::getPaymentFinishTime, param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime());
        }
        //其他索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradePaymentOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getMerchantName()), TradePaymentOrder::getMerchantName, param.getMerchantName())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePaymentOrder::getPaymentStatus, param.getPaymentStatus());

        //非索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradePaymentOrder::getPaymentMethod, param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelName()), TradePaymentOrder::getChannelCode, param.getChannelName())
                .ge(Objects.nonNull(param.getAmountMin()), TradePaymentOrder::getAmount, param.getAmountMin())
                .le(Objects.nonNull(param.getAmountMax()), TradePaymentOrder::getAmount, param.getAmountMax())
                .eq(Objects.nonNull(param.getTradeStatus()), TradePaymentOrder::getTradeStatus, param.getTradeStatus());
        //排序
        queryWrapper.lambda().orderByDesc(TradePaymentOrder::getTradeTime);
        Page<TradePaymentOrder> page = tradePaymentOrderService.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
        if (page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TradePayOrderPageDTO> collect = page.getRecords().stream()
                .map(this::getTradePayOrderPageDTO)
                .toList();
        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }


    @Override
    @SneakyThrows
    public String exportPayOrderList(TradePayOrderPageParam param) {
        log.info("exportPayOrderList param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }

        int limitSize = 5000;
        // 统计数量
        QueryWrapper<TradePaymentOrder> countWrapper = new QueryWrapper<>();
        //单号索引
        countWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradePaymentOrder::getTradeNo, param.getTradeNo())
                .in(CollectionUtils.isNotEmpty(param.getTradeNoList()), TradePaymentOrder::getTradeNo, param.getTradeNoList())
                .eq(StringUtils.isNotBlank(param.getOuterNo()), TradePaymentOrder::getOrderNo, param.getOuterNo())
                .in(CollectionUtils.isNotEmpty(param.getOuterNoList()), TradePaymentOrder::getOrderNo, param.getOuterNoList());
        //时间组合索引
        if (StringUtils.isNoneBlank(param.getTradeStartTime(), param.getTradeEndTime())) {
            countWrapper.lambda().between(TradePaymentOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        if (StringUtils.isNoneBlank(param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime())) {
            countWrapper.lambda().between(TradePaymentOrder::getPaymentFinishTime, param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime());
        }
        //其他索引
        countWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradePaymentOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getMerchantName()), TradePaymentOrder::getMerchantName, param.getMerchantName())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePaymentOrder::getPaymentStatus, param.getPaymentStatus());

        //非索引
        countWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradePaymentOrder::getPaymentMethod, param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelName()), TradePaymentOrder::getChannelCode, param.getChannelName())
                .ge(Objects.nonNull(param.getAmountMin()), TradePaymentOrder::getAmount, param.getAmountMin())
                .le(Objects.nonNull(param.getAmountMax()), TradePaymentOrder::getAmount, param.getAmountMax())
                .eq(Objects.nonNull(param.getTradeStatus()), TradePaymentOrder::getTradeStatus, param.getTradeStatus());
        long count = tradePaymentOrderService.count(countWrapper);
        log.info("exportPayOrderList count={}", count);
        if (count == 0) {
            throw new PaymentException("There is no data to export, please confirm");
        }
        if (count > limitSize) {
            throw new PaymentException("The amount of payin data exported is too large, please contact our supporters");
        }


        // 查询
        QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
        //单号索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradePaymentOrder::getTradeNo, param.getTradeNo())
                .in(CollectionUtils.isNotEmpty(param.getTradeNoList()), TradePaymentOrder::getTradeNo, param.getTradeNoList())
                .eq(StringUtils.isNotBlank(param.getOuterNo()), TradePaymentOrder::getOrderNo, param.getOuterNo())
                .in(CollectionUtils.isNotEmpty(param.getOuterNoList()), TradePaymentOrder::getOrderNo, param.getOuterNoList());
        //时间组合索引
        if (StringUtils.isNoneBlank(param.getTradeStartTime(), param.getTradeEndTime())) {
            queryWrapper.lambda().between(TradePaymentOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        if (StringUtils.isNoneBlank(param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime())) {
            queryWrapper.lambda().between(TradePaymentOrder::getPaymentFinishTime, param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime());
        }
        //其他索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradePaymentOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getMerchantName()), TradePaymentOrder::getMerchantName, param.getMerchantName())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePaymentOrder::getPaymentStatus, param.getPaymentStatus());

        //非索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradePaymentOrder::getPaymentMethod, param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelName()), TradePaymentOrder::getChannelCode, param.getChannelName())
                .ge(Objects.nonNull(param.getAmountMin()), TradePaymentOrder::getAmount, param.getAmountMin())
                .le(Objects.nonNull(param.getAmountMax()), TradePaymentOrder::getAmount, param.getAmountMax())
                .eq(Objects.nonNull(param.getTradeStatus()), TradePaymentOrder::getTradeStatus, param.getTradeStatus());
        List<TradePaymentOrder> payOrderList = tradePaymentOrderService.list(queryWrapper);
        List<TradePayOrderCsvDTO> csvDTOList = payOrderList.stream().map(e -> {
            String tradeStatus = TradeStatusEnum.codeToEnum(e.getTradeStatus()).getMerchantStatus();
//            String paymentStatus = PaymentStatusEnum.codeToEnum(e.getPaymentStatus()).getMerchantStatus();
//            String tradeTime = Objects.nonNull(e.getTradeTime()) ? e.getTradeTime().format(TradeConstant.DF_0) : "";
//            String paymentFinishTime = Objects.nonNull(e.getPaymentFinishTime()) ?
//                    e.getPaymentFinishTime().format(TradeConstant.DF_0) : "";

            TradePayOrderCsvDTO csvDTO = new TradePayOrderCsvDTO();
            BeanUtils.copyProperties(e, csvDTO);
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setOuterNo("'" + e.getOrderNo());
            csvDTO.setAccountNo("'" + e.getAccountNo());
            csvDTO.setTradeStatus(tradeStatus);
//            csvDTO.setPaymentStatus(paymentStatus);
//            csvDTO.setTradeTime(tradeTime);
//            csvDTO.setPaymentFinishTime(paymentFinishTime);
            csvDTO.setMerchantFee(e.getMerchantFee());
            return csvDTO;
        }).toList();

        StringWriter stringWriter = new StringWriter();
        CsvWriter csvWriter = CsvUtil.getWriter(stringWriter);

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("payin-");
        String uploadObject = storageHandler.uploadObject(csvDTOList, param.getOperator(), fileName, "PayOrderList");
        log.info("exportPayOrderList uploadObject={}", uploadObject);
        return uploadObject;
    }

    *//**
     * 体验收银台 & 正式收银台
     *//*
    @Override
    public CashierDTO getCashier(CashierParam param) {
        log.info("getCashier param={}", JSONUtil.toJsonStr(param));

        String tradeNo = param.getTradeNo();
        String timestamp = param.getTimestamp();
        String token = param.getToken();

        // 校验参数
        if (StringUtils.isNoneBlank(timestamp, token)) {
            String cashierToken = AesUtils.cashierToken(tradeNo + timestamp);
            Assert.equals(token, cashierToken, () -> new PaymentException(PAY_INVALID_TOKEN, tradeNo));
        }

        // 校验订单
        QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_SQL);
        TradePaymentOrder order = tradePaymentOrderService.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.PAY_ORDER_NOT_EXIST, tradeNo));

        CashierDTO dto = new CashierDTO();
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        log.info("getCashier tradeStatusEnum={}", tradeStatusEnum.name());

        switch (tradeStatusEnum) {
            case TRADE_INIT -> initPaymentMethod(order, dto);
            case TRADE_SUCCESS -> successPaymentMethod(order, dto);
            case TRADE_FAILED -> throw new PaymentException(ExceptionCode.PAY_ORDER_HAS_FAILED, order.getTradeNo());
            case TRADE_EXPIRED -> throw new PaymentException(ExceptionCode.PAY_ORDER_HAS_EXPIRED, order.getTradeNo());
            default -> throw new PaymentException(ExceptionCode.PAY_ORDER_STATUS_INVALID, order.getTradeNo());
        }

        dto.setTradeNo(order.getTradeNo());
//        dto.setTradeTime(order.getTradeTime().format(TradeConstant.DF_0));
//        dto.setItemDetailInfo(order.getItemDetailInfo());
        dto.setMerchantId(order.getMerchantId());
        dto.setMerchantName(order.getMerchantName());
        dto.setCurrency(order.getCurrency());
        dto.setAmount(order.getAmount());
        dto.setExpiryPeriod(getExpiryPeriod(order));
        dto.setPaymentStatus(order.getPaymentStatus());
        return dto;
    }

    *//**
     * 代收订单详情
     *//*

    @Override
    public TradePayOrderDTO getPayOrderByTradeNo(String tradeNo) {
        log.info("getPayOrderByTradeNo param={}", tradeNo);

        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_SQL);
        TradePaymentOrder order = tradePaymentOrderService.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.PAY_ORDER_NOT_EXIST, tradeNo));

        TradePayOrderDetailDTO detailDTO = getTradePayOrderDetailDTO(order);
        List<TradeOrderTimeLineDTO> sortLineDTOList = getTradePayOrderTimeLineList(order);

        TradePayOrderDTO payOrderDTO = new TradePayOrderDTO();
        payOrderDTO.setPayOrderDetail(detailDTO);
        payOrderDTO.setTimeLine(sortLineDTOList);
        return payOrderDTO;
    }

    *//**
     * 组装订单列表
     *//*
    private TradePayOrderPageDTO getTradePayOrderPageDTO(TradePaymentOrder order) {
        ChannelResultDTO channelResult = parseChannelResult(order.getTradeResult());
        PayerDTO payerDTO = Optional.of(order).map(TradePaymentOrder::getPayerInfo)
                .map(e -> JSONUtil.toBean(e, PayerDTO.class))
                .orElse(null);

        TradePayOrderPageDTO pageDTO = new TradePayOrderPageDTO();
        pageDTO.setPurpose(order.getPurpose());
        pageDTO.setTradeNo(order.getTradeNo());
        pageDTO.setOuterNo(order.getOrderNo());
        pageDTO.setChannelOrderNo(channelResult.getChannelOrderNo()); // 解析
        pageDTO.setPaymentMethod(order.getPaymentMethod());
        pageDTO.setChannelName(order.getChannelCode());
        pageDTO.setMerchantId(order.getMerchantId());
        pageDTO.setMerchantName(order.getMerchantName());
        pageDTO.setAmount(order.getAmount());
        pageDTO.setMerchantFee(order.getMerchantFee());
        pageDTO.setTradeStatus(order.getTradeStatus());
        pageDTO.setPaymentStatus(order.getPaymentStatus());
        pageDTO.setSettleStatus(order.getSettleStatus());
        pageDTO.setCallBackStatus(order.getCallBackStatus());
//        pageDTO.setTradeTime(order.getTradeTime());
//        pageDTO.setPaymentFinishTime(order.getPaymentFinishTime());
        pageDTO.setSource(order.getSource());
        pageDTO.setPayer(payerDTO);
        return pageDTO;
    }

    *//**
     * 组装订单明细
     *//*
    private TradePayOrderDetailDTO getTradePayOrderDetailDTO(TradePaymentOrder order) {
        ChannelResultDTO channelResult = parseChannelResult(order.getTradeResult());
        PayerDTO payer = Optional.of(order).map(TradePaymentOrder::getPayerInfo)
                .map(e -> JSONUtil.toBean(e, PayerDTO.class))
                .orElse(null);

        TradePayOrderDetailDTO detailDTO = new TradePayOrderDetailDTO();
        detailDTO.setTradeNo(order.getTradeNo());
        detailDTO.setOuterNo(order.getOrderNo());
        detailDTO.setChannelOrderNo(channelResult.getChannelOrderNo()); // 需要解析
        detailDTO.setPurpose(order.getPurpose());
        detailDTO.setPaymentMethod(order.getPaymentMethod());
        detailDTO.setChannelCode(order.getChannelCode());
        detailDTO.setChannelName(order.getChannelCode());
        detailDTO.setMerchantId(order.getMerchantId());
        detailDTO.setMerchantName(order.getMerchantName());
        detailDTO.setAccountNo(order.getAccountNo());

        detailDTO.setCurrency(order.getCurrency());
        detailDTO.setAmount(order.getAmount());
        detailDTO.setMerchantFee(order.getMerchantFee());
        detailDTO.setAccountAmount(order.getAccountAmount());
        detailDTO.setChannelCost(order.getChannelCost());

        detailDTO.setPaymentStatus(Objects.isNull(order.getPaymentStatus()) ?
                PaymentStatusEnum.PAYMENT_PROCESSING.getCode() :
                order.getPaymentStatus());
        detailDTO.setCallBackStatus(Objects.isNull(order.getCallBackStatus()) ?
                CallBackStatusEnum.CALLBACK_TODO.getCode() :
                order.getCallBackStatus());
        detailDTO.setSettleStatus(Objects.isNull(order.getSettleStatus()) ?
                SettleStatusEnum.SETTLE_TODO.getCode() :
                order.getSettleStatus());
//        detailDTO.setTradeTime(order.getTradeTime());
//        detailDTO.setPaymentFinishTime(order.getPaymentFinishTime());
        detailDTO.setPayer(payer);
        return detailDTO;
    }

    *//**
     * 组装订单时间轴
     *//*
    @SneakyThrows
    private List<TradeOrderTimeLineDTO> getTradePayOrderTimeLineList(TradePaymentOrder order) {
        List<TradeOrderTimeLineDTO> lineDTOList = new ArrayList<>();

        // 创建订单
        TradeOrderTimeLineDTO createLine = new TradeOrderTimeLineDTO();
        createLine.setLineTime(order.getCreateTime());
        createLine.setLineMessage("Payin order receive request: SUCCESS");
        createLine.setStatus(true);
        lineDTOList.add(createLine);

        // 下单
        CompletableFuture<Void> f0 = CompletableFuture.runAsync(() -> {
            if (Objects.isNull(order.getTradeTime())) {
                return;
            }
            TradeOrderTimeLineDTO paymentLine = new TradeOrderTimeLineDTO();
//            paymentLine.setLineTime(order.getTradeTime());
            TradeStatusEnum statusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());

            // 失败
            if (TradeStatusEnum.TRADE_FAILED.equals(statusEnum)) {
                paymentLine.setStatus(false);
                String message = "Payin order palace order: " + statusEnum.getMerchantStatus();
                String error = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                        .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                        .map(TradeResultDTO::getError)
                        .map(this::getPayErrorMsg)
                        .orElse("");

                message = message + " | error: " + error;
                paymentLine.setLineMessage(message);
            }

            // 成功
            if (TradeStatusEnum.TRADE_SUCCESS.equals(statusEnum)) {
                paymentLine.setStatus(true);
                String message = "Payin order palace order: " + statusEnum.getMerchantStatus();
                paymentLine.setLineMessage(message);
            }
            lineDTOList.add(paymentLine);
        });

        // 支付
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            if (Objects.isNull(order.getPaymentStatus()) || Objects.isNull(order.getPaymentFinishTime())
                    || 0 == order.getPaymentStatus()) {
                return;
            }

            PaymentResultAttributeDTO dto = Optional.ofNullable(order.getAttribute())
                    .map(e -> JSONUtil.toBean(e, PaymentResultAttributeDTO.class))
                    .orElse(new PaymentResultAttributeDTO());
            String type = dto.getType();
            String operator = dto.getOperator();

            PaymentStatusEnum statusEnum = PaymentStatusEnum.codeToEnum(order.getPaymentStatus());
            StringBuilder message =
                    new StringBuilder("Payin order paid: ").append(statusEnum.getName());
            if (StringUtils.isNotBlank(type)) {
                message.append(". Operator type: ").append(type);
            }
            TradeOrderTimeLineDTO paymentLine = new TradeOrderTimeLineDTO();
//            paymentLine.setLineTime(order.getPaymentFinishTime());
            paymentLine.setLineMessage(message.toString());
            paymentLine.setStatus(PaymentStatusEnum.PAYMENT_SUCCESS.getCode().equals(order.getPaymentStatus()));
            paymentLine.setLineOperator(operator);
            lineDTOList.add(paymentLine);
        });

        // 回调
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            QueryWrapper<TradePaymentCallBackResult> callbackResultQuery = new QueryWrapper<>();
            callbackResultQuery.lambda().eq(TradePaymentCallBackResult::getTradeNo, order.getTradeNo());
            List<TradePaymentCallBackResult> callbackResultList = tradePaymentCallBackResultService.list(callbackResultQuery);
            if (CollectionUtils.isNotEmpty(callbackResultList)) {
                List<TradeOrderTimeLineDTO> collect = callbackResultList.stream().map(e -> {
                    String operator = Optional.ofNullable(e.getAttribute())
                            .map(f -> JSONUtil.toBean(f, TradeCallBackResultAttributeDTO.class))
                            .map(TradeCallBackResultAttributeDTO::getOperator)
                            .orElse(TradeConstant.SYSTEM);
                    String message = Optional.ofNullable(e.getCallBackResult())
                            .map(f -> JSONUtil.toBean(f, TradeCallBackResultDTO.class))
                            .map(TradeCallBackResultDTO::getMessage)
                            .orElse("");

                    CallBackStatusEnum statusEnum = CallBackStatusEnum.codeToEnum(e.getCallBackStatus());
                    TradeOrderTimeLineDTO callbackLine = new TradeOrderTimeLineDTO();
//                    callbackLine.setLineTime(e.getCallBackTime());
                    callbackLine.setLineMessage("Payin order callback merchant: " + message);
                    callbackLine.setStatus(CallBackStatusEnum.CALLBACK_SUCCESS.equals(statusEnum));
                    callbackLine.setLineOperator(operator);
                    return callbackLine;
                }).toList();
                lineDTOList.addAll(collect);
            }
        });

        // 清结算，如果支付成功
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
            if (Objects.isNull(order.getSettleStatus()) || Objects.isNull(order.getSettleFinishTime())
                    || 0 == order.getSettleStatus()) {
                return;
            }

            PaymentResultAttributeDTO dto = Optional.ofNullable(order.getAttribute())
                    .map(e -> JSONUtil.toBean(e, PaymentResultAttributeDTO.class))
                    .orElse(new PaymentResultAttributeDTO());
            String type = dto.getType();
            String operator = dto.getOperator();
            StringBuilder message = new StringBuilder("Payin order settlement complete");
            if (StringUtils.isNotBlank(type)) {
                message.append(". Operator type : ").append(type);
            }
            TradeOrderTimeLineDTO settlementLine = new TradeOrderTimeLineDTO();
//            settlementLine.setLineTime(order.getSettleFinishTime());
            settlementLine.setLineMessage(message.toString());
            settlementLine.setStatus(SettleStatusEnum.SETTLE_SUCCESS.getCode().equals(order.getSettleStatus()));
            settlementLine.setLineOperator(operator);
            lineDTOList.add(settlementLine);
        });

        CompletableFuture.allOf(f0, f1, f2, f3).join();
        log.info("lineDTOList={}", JSONUtil.toJsonStr(lineDTOList));

        return lineDTOList.stream()
                .sorted(Comparator.comparing(TradeOrderTimeLineDTO::getLineTime))
                .toList();
    }

    *//**
     * 查询支付方式
     *//*
    private void initPaymentMethod(TradePaymentOrder order, CashierDTO dto) {
        String merchantId = order.getMerchantId();

        // 校验此收银台链接订单是否超时
        checkExpiredAgain(order);

        // 并行查询
        CompletableFuture<List<PaymentMethodDTO>> cf0
                = CompletableFuture.supplyAsync(this::getPaymentMethodList4Transaction);
        CompletableFuture<MerchantPayPaymentConfigSettingDTO> cf1
                = CompletableFuture.supplyAsync(() -> getMerchantPayPaymentConfigList(merchantId));
        CompletableFuture.allOf(cf0, cf1).join();

        // 获取结果
        List<PaymentMethodDTO> channelMethodDTOList;
        MerchantPayPaymentConfigSettingDTO merchantPayPaymentConfigSetting;
        try {
            channelMethodDTOList = cf0.get();
            merchantPayPaymentConfigSetting = cf1.get();
        } catch (InterruptedException e) {
            log.error("initPaymentMethod interrupted exception:", e);
            Thread.currentThread().interrupt();
            return;
        } catch (Exception e) {
            log.error("initPaymentMethod exception:", e);
            return;
        }

        // 商户支持的支付方式
        List<MerchantPayPaymentConfigDTO> paymentConfigList = merchantPayPaymentConfigSetting.getMerchantPayPaymentConfigDTOList();
        log.info("initPaymentMethod paymentConfigList size={}", paymentConfigList.size());
        log.info("initPaymentMethod channelMethodDTOList size={}", channelMethodDTOList.size());

        // 如果商户支持集合为空 或者渠道支持为空, 则返回
        if (CollectionUtils.isEmpty(paymentConfigList) || CollectionUtils.isEmpty(channelMethodDTOList)) {
            return;
        }

        // 商户支持的支付方式Str
        Set<String> merchantChannelConfigSet = paymentConfigList.stream()
                .map(MerchantPayPaymentConfigDTO::getPaymentMethod)
                .collect(Collectors.toSet());
        log.info("initPaymentMethod merchantChannelConfigSet={}", JSONUtil.toJsonStr(merchantChannelConfigSet));

        // 支付链接配置
        MerchantPaymentLinkSettingDTO paymentLinkSetting = merchantPayPaymentConfigSetting.getMerchantPaymentLinkSetting();

        // 推荐支付方式
        List<String> recommendedPaymentMethod = Optional.ofNullable(paymentLinkSetting)
                .map(MerchantPaymentLinkSettingDTO::getRecommendedPaymentMethod)
                .orElse(new ArrayList<>());

        // 排序支付方式配置
        List<PaymentMethodSortedDTO> sortedPaymentMethodList = Optional.ofNullable(paymentLinkSetting)
                .map(MerchantPaymentLinkSettingDTO::getSortedPaymentMethodList)
                .orElse(new ArrayList<>());
        List<Integer> sortPaymentTypeList = sortedPaymentMethodList.stream()
                .map(PaymentMethodSortedDTO::getPaymentType)
                .toList();

        // 样式
        TradeCashierStyleDTO styleDTO = Optional.ofNullable(paymentLinkSetting).map(e -> {
            TradeCashierStyleDTO cashierStyleDTO = new TradeCashierStyleDTO();
            cashierStyleDTO.setLogo(e.getLogo());
            cashierStyleDTO.setBgColor(e.getBgColor());
            return cashierStyleDTO;
        }).orElse(null);
        log.info("initPaymentMethod styleDTO={}", JSONUtil.toJsonStr(styleDTO));
        dto.setStyle(styleDTO);

        // 可能现阶段订单也已经存在支付方式. 譬如OVO订单. fix 20231016
        String paymentMethod = order.getPaymentMethod();
        log.info("initPaymentMethod paymentMethod=[{}]", paymentMethod);
        if (StringUtils.isNotBlank(paymentMethod)) {
            CashierPaymentTypeDTO typeDTO = new CashierPaymentTypeDTO();
            PaymentMethodDTO paymentMethodDTO = channelMethodDTOList.stream()
                    .filter(e -> merchantChannelConfigSet.contains(e.getPaymentMethod()))
                    .filter(e -> e.getPaymentMethod().equals(paymentMethod))
                    .findAny()
                    .orElse(null);
            if (Objects.nonNull(paymentMethodDTO)) {
                CashierPaymentMethodDTO methodDTO = applicationConverter.convertCashierPaymentDTO(paymentMethodDTO);
                typeDTO.setPaymentType(paymentMethodDTO.getPaymentType());
                typeDTO.setPaymentMethodList(Collections.singletonList(methodDTO));
                typeDTO.setSort(0);
                dto.setPaymentTypeList(Collections.singletonList(typeDTO));
                return;
            }
        }

        // 构建推荐支付方式 效率不高
        List<CashierPaymentMethodDTO> recommendedMethod = channelMethodDTOList.stream()
                .filter(e -> merchantChannelConfigSet.contains(e.getPaymentMethod()))
                .filter(e -> recommendedPaymentMethod.contains(e.getPaymentMethod()))
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = applicationConverter.convertCashierPaymentDTO(e);
                    int indexOf = recommendedPaymentMethod.indexOf(e.getPaymentMethod());
                    indexOf = indexOf >= 0 ? indexOf : 99;
                    methodDTO.setSort(indexOf);

                    // 随机找到一个
                    MerchantPayPaymentConfigDTO merchantPayPaymentConfigDTO = paymentConfigList.stream()
                            .filter(p -> p.getPaymentMethod().equals(e.getPaymentMethod()))
                            .findAny()
                            .orElseThrow(() -> new PaymentException(ExceptionCode.EXTERNAL_SERVER_ERROR));
                    methodDTO.setSingleFee(merchantPayPaymentConfigDTO.getSingleFee());
                    methodDTO.setSingleRate(merchantPayPaymentConfigDTO.getSingleRate());
                    return methodDTO;
                })
                .sorted(Comparator.comparing(CashierPaymentMethodDTO::getSort))
                .toList();
        log.info("initPaymentMethod recommendedMethod={}", JSONUtil.toJsonStr(recommendedMethod));


        List<CashierPaymentTypeDTO> paymentTypeList = new ArrayList<>();
        // 求其交集
        // VA
        List<CashierPaymentMethodDTO> vaMethodList = channelMethodDTOList.stream()
                .filter(e -> e.getPaymentType().equals(PaymentTypeEnum.VIRTUAL_ACCOUNT.getCode()))
                .filter(e -> merchantChannelConfigSet.contains(e.getPaymentMethod()))
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = applicationConverter.convertCashierPaymentDTO(e);

                    // 随机找到一个
                    MerchantPayPaymentConfigDTO merchantConfig = paymentConfigList.stream()
                            .filter(p -> p.getPaymentMethod().equals(e.getPaymentMethod()))
                            .findAny()
                            .orElseThrow(() -> new PaymentException(ExceptionCode.EXTERNAL_SERVER_ERROR));

                    return getCashierPaymentMethodDTO(sortedPaymentMethodList, methodDTO,
                            PaymentTypeEnum.VIRTUAL_ACCOUNT, merchantConfig, e);
                })
                .sorted(Comparator.comparing(CashierPaymentMethodDTO::getSort))
                .toList();
        int vaSort = sortPaymentTypeList.indexOf(PaymentTypeEnum.VIRTUAL_ACCOUNT.getCode());
        vaSort = vaSort >= 0 ? vaSort : 99;
        CashierPaymentTypeDTO vaPaymentType = new CashierPaymentTypeDTO();
        vaPaymentType.setPaymentType(PaymentTypeEnum.VIRTUAL_ACCOUNT.getCode());
        vaPaymentType.setPaymentMethodList(vaMethodList);
        vaPaymentType.setSort(vaSort);
        paymentTypeList.add(vaPaymentType);

        // QRIS
        List<CashierPaymentMethodDTO> qrisMethodList = channelMethodDTOList.stream()
                .filter(e -> e.getPaymentType().equals(PaymentTypeEnum.QRIS.getCode()))
                .filter(e -> merchantChannelConfigSet.contains(e.getPaymentMethod()))
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = applicationConverter.convertCashierPaymentDTO(e);

                    // 随机找到一个
                    MerchantPayPaymentConfigDTO merchantConfig = paymentConfigList.stream()
                            .filter(p -> p.getPaymentMethod().equals(e.getPaymentMethod()))
                            .findAny()
                            .orElseThrow(() -> new PaymentException(ExceptionCode.EXTERNAL_SERVER_ERROR));

                    return getCashierPaymentMethodDTO(sortedPaymentMethodList, methodDTO, PaymentTypeEnum.QRIS,
                            merchantConfig, e);
                })
                .sorted(Comparator.comparing(CashierPaymentMethodDTO::getSort))
                .toList();
        int qrisSort = sortPaymentTypeList.indexOf(PaymentTypeEnum.QRIS.getCode());
        qrisSort = qrisSort >= 0 ? qrisSort : 99;
        CashierPaymentTypeDTO qrisPaymentType = new CashierPaymentTypeDTO();
        qrisPaymentType.setPaymentType(PaymentTypeEnum.QRIS.getCode());
        qrisPaymentType.setPaymentMethodList(qrisMethodList);
        qrisPaymentType.setSort(qrisSort);
        paymentTypeList.add(qrisPaymentType);

        // Wallet
        List<CashierPaymentMethodDTO> walletMethodList = channelMethodDTOList.stream()
                .filter(e -> e.getPaymentType().equals(PaymentTypeEnum.E_WALLET.getCode()))
                .filter(e -> merchantChannelConfigSet.contains(e.getPaymentMethod()))
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = applicationConverter.convertCashierPaymentDTO(e);

                    // 随机找到一个
                    MerchantPayPaymentConfigDTO merchantConfig = paymentConfigList.stream()
                            .filter(p -> p.getPaymentMethod().equals(e.getPaymentMethod()))
                            .findAny()
                            .orElseThrow(() -> new PaymentException(ExceptionCode.EXTERNAL_SERVER_ERROR));

                    return getCashierPaymentMethodDTO(sortedPaymentMethodList, methodDTO, PaymentTypeEnum.E_WALLET,
                            merchantConfig, e);
                })
                .sorted(Comparator.comparing(CashierPaymentMethodDTO::getSort))
                .toList();
        int walletSort = sortPaymentTypeList.indexOf(PaymentTypeEnum.E_WALLET.getCode());
        walletSort = walletSort > 0 ? walletSort : 99;
        CashierPaymentTypeDTO walletPaymentType = new CashierPaymentTypeDTO();
        walletPaymentType.setPaymentType(PaymentTypeEnum.E_WALLET.getCode());
        walletPaymentType.setPaymentMethodList(walletMethodList);
        walletPaymentType.setSort(walletSort);
        paymentTypeList.add(walletPaymentType);

        // retailStore
        *//*List<CashierPaymentMethodDTO> retailStoreMethodList = channelMethodDTOList.stream()
                .filter(e -> e.getPaymentType().equals(PaymentTypeEnum.RETAILSTORE.getCode()))
                .filter(e -> merchantChannelConfigSet.contains(e.getPaymentMethod()))
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = applicationConverter.convertCashierPaymentDTO(e);

                    // 随机找到一个
                    MerchantPayPaymentConfigDTO merchantConfig = paymentConfigList.stream()
                            .filter(p -> p.getPaymentMethod().equals(e.getPaymentMethod()))
                            .findAny()
                            .orElseThrow(() -> new PaymentException(ExceptionCode.EXTERNAL_SERVER_ERROR));

                    return getCashierPaymentMethodDTO(sortedPaymentMethodList, methodDTO, PaymentTypeEnum.RETAILSTORE
                            , merchantConfig, e);
                })
                .sorted(Comparator.comparing(CashierPaymentMethodDTO::getSort))
                .toList();
        int storeSort = sortPaymentTypeList.indexOf(PaymentTypeEnum.RETAILSTORE.getCode());
        storeSort = storeSort >= 0 ? storeSort : 99;
        CashierPaymentTypeDTO cardPaymentType = new CashierPaymentTypeDTO();
        cardPaymentType.setPaymentType(PaymentTypeEnum.RETAILSTORE.getCode());
        cardPaymentType.setPaymentMethodList(retailStoreMethodList);
        cardPaymentType.setSort(storeSort);
        paymentTypeList.add(cardPaymentType);*//*

        // creditCard
        List<CashierPaymentMethodDTO> creditCardMethodList = channelMethodDTOList.stream()
                .filter(e -> e.getPaymentType().equals(PaymentTypeEnum.CREDIT_CARD.getCode()))
                .filter(e -> merchantChannelConfigSet.contains(e.getPaymentMethod()))
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = applicationConverter.convertCashierPaymentDTO(e);

                    // 随机找到一个
                    MerchantPayPaymentConfigDTO merchantConfig = paymentConfigList.stream()
                            .filter(p -> p.getPaymentMethod().equals(e.getPaymentMethod()))
                            .findAny()
                            .orElseThrow(() -> new PaymentException(ExceptionCode.EXTERNAL_SERVER_ERROR));

                    return getCashierPaymentMethodDTO(sortedPaymentMethodList, methodDTO, PaymentTypeEnum.CREDIT_CARD
                            , merchantConfig, e);
                })
                .sorted(Comparator.comparing(CashierPaymentMethodDTO::getSort))
                .toList();
        int cardSort = sortPaymentTypeList.indexOf(PaymentTypeEnum.CREDIT_CARD.getCode());
        cardSort = cardSort >= 0 ? cardSort : 99;
        CashierPaymentTypeDTO storePaymentType = new CashierPaymentTypeDTO();
        storePaymentType.setPaymentType(PaymentTypeEnum.CREDIT_CARD.getCode());
        storePaymentType.setPaymentMethodList(creditCardMethodList);
        storePaymentType.setSort(cardSort);
        paymentTypeList.add(storePaymentType);

        // 支付大类排序
        List<CashierPaymentTypeDTO> sortPaymentTypeMethodList = paymentTypeList.stream()
                .sorted(Comparator.comparing(CashierPaymentTypeDTO::getSort))
                .toList();

        dto.setRecommendedMethod(recommendedMethod);
        dto.setPaymentTypeList(sortPaymentTypeMethodList);
    }


    *//**
     * 再次确认是否超时
     *//*
    private void checkExpiredAgain(TradePaymentOrder order) {
        Integer expiryPeriod = Optional.of(order).map(TradePaymentOrder::getAttribute)
                .map(e -> JSONUtil.toBean(e, TradePaymentAttributeDTO.class))
                .map(TradePaymentAttributeDTO::getExpiryPeriod)
                .orElse(TradeConstant.TRADE_EXPIRY_PERIOD_MAX);
//        long seconds = Duration.between(order.getTradeTime(), LocalDateTime.now()).getSeconds();
//        log.info("doCashierPay expiryPeriod={}, seconds={}", expiryPeriod, seconds);
//        if (seconds > expiryPeriod) {
//            Integer tradeStatus = TradeStatusEnum.TRADE_EXPIRED.getCode();
//            UpdateWrapper<TradePaymentOrder> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.lambda().set(TradePaymentOrder::getTradeStatus, tradeStatus)
//                    .eq(TradePaymentOrder::getId, order.getId());
//            tradePaymentOrderService.update(updateWrapper);
//
//             订单来源
//            TradePaySourceEnum tradePaySourceEnum = TradePaySourceEnum.codeToEnum(order.getSource());
//            log.info("doCashierPay tradePaySourceEnum={}", tradePaySourceEnum.name());
//            if (TradePaySourceEnum.PAY_LINK.equals(tradePaySourceEnum)) {
//                Integer paymentLinkStatus = TradePaymentLinkStatusEnum.PAYMENT_LINK_EXPIRED.getCode();
//                UpdateWrapper<TradePaymentLinkOrder> linkUpdate = new UpdateWrapper<>();
//                linkUpdate.lambda().set(TradePaymentLinkOrder::getLinkStatus, paymentLinkStatus)
//                        .eq(TradePaymentLinkOrder::getLinkNo, order.getOuterNo());
//                tradePaymentLinkOrderService.update(linkUpdate);
//            }
//            throw new PaymentException(ExceptionCode.PAY_ORDER_HAS_EXPIRED, order.getTradeNo());
//        }
    }

    *//**
     * 查询商户配置的支付方式, 有缓存加持
     *//*
    @SneakyThrows
    private MerchantPayPaymentConfigSettingDTO getMerchantPayPaymentConfigList(String merchantId) {
        // 先查询缓存
        String key = TradeConstant.CACHE_MERCHANT_PAYMENT_SETTING + merchantId;
        Object obj = redisService.get(key);
        MerchantPayPaymentConfigSettingDTO configSettingDTO = Optional.ofNullable(obj)
                .map(Object::toString)
                .map(e -> JSONUtil.toBean(e, MerchantPayPaymentConfigSettingDTO.class))
                .orElse(null);

        if (Objects.isNull(configSettingDTO)) {
            // 商户渠道配置-代收 & 可用
            MerchantPayPaymentConfigParam configParam = new MerchantPayPaymentConfigParam();
            configParam.setMerchantId(merchantId);
            configParam.setStatus(Boolean.TRUE);
            configSettingDTO = BaseResult.parse(merchantApiService.getMerchantPayPaymentConfigListAndSetting(configParam).toFuture().join());
            if (Objects.isNull(configSettingDTO)
                    || CollectionUtils.isEmpty(configSettingDTO.getMerchantPayPaymentConfigDTOList())) {
                return new MerchantPayPaymentConfigSettingDTO();
            }
            // 存入缓存
            redisService.set(key, JSONUtil.toJsonStr(configSettingDTO), 60);

        }

        return configSettingDTO;
    }

    *//**
     * 根据配置进行排序
     *//*
    private CashierPaymentMethodDTO getCashierPaymentMethodDTO(List<PaymentMethodSortedDTO> sortedPaymentMethodList,
                                                               CashierPaymentMethodDTO methodDTO,
                                                               PaymentTypeEnum paymentTypeEnum,
                                                               MerchantPayPaymentConfigDTO merchantConfig,
                                                               PaymentMethodDTO paymentMethodDTO) {
        methodDTO.setSingleFee(merchantConfig.getSingleFee());
        methodDTO.setSingleRate(merchantConfig.getSingleRate());

        if (CollectionUtils.isEmpty(sortedPaymentMethodList)) {
            methodDTO.setSort(0);
            return methodDTO;
        }
        int indexOf = sortedPaymentMethodList.stream()
                .filter(f -> f.getPaymentType().equals(paymentTypeEnum.getCode()))
                .map(PaymentMethodSortedDTO::getPaymentMethodList)
                .map(f -> f.indexOf(paymentMethodDTO.getPaymentMethod()))
                .findAny()
                .orElse(-1);
        indexOf = indexOf >= 0 ? indexOf : 99;
        methodDTO.setSort(indexOf);
        return methodDTO;
    }

    *//**
     * 查询支付配置, 有缓存加持
     *//*
    @SneakyThrows
    private List<PaymentMethodDTO> getPaymentMethodList4Transaction() {
        // 先通过缓存
        String key = TradeConstant.CACHE_TRANSACTION_PAYMENT;
        Object obj = redisService.get(key);
        List<PaymentMethodDTO> paymentMethodDTOList = Optional.ofNullable(obj)
                .map(Object::toString)
                .map(e -> JSONUtil.toList(e, PaymentMethodDTO.class))
                .orElse(Collections.emptyList());
        log.info("getPaymentMethodList4Transaction cache key={} size={}", key, paymentMethodDTOList.size());

        // 缓存存在, 直接返回
        if (CollectionUtils.isNotEmpty(paymentMethodDTOList)) {
            return paymentMethodDTOList;
        } else {
            List<PaymentMethodDTO> dtoList = BaseResult.parse(paymentApiService.getPaymentMethodList4Transaction().toFuture().join());
            if (CollectionUtils.isEmpty(dtoList)) {
                return Collections.emptyList();
            }

            redisService.set(key, JSONUtil.toJsonStr(dtoList), 60);
            return dtoList;
        }
    }


    *//**
     * 计算过期时间间隔（秒数）
     *//*
    private long getExpiryPeriod(TradePaymentOrder order) {
//        LocalDateTime tradeTime = Optional.of(order).map(TradePaymentOrder::getTradeTime).orElse(null);
//        if (Objects.isNull(tradeTime)) {
//            return 0;
//        }

        Integer tradeExpiryPeriod = Optional.of(order).map(TradePaymentOrder::getAttribute)
                .map(e -> JSONUtil.toBean(e, TradePaymentAttributeDTO.class))
                .map(TradePaymentAttributeDTO::getExpiryPeriod)
                .orElse(TradeConstant.TRADE_EXPIRY_PERIOD_MAX);

//        LocalDateTime expiryTime = tradeTime.plusSeconds(tradeExpiryPeriod);
//        Duration duration = Duration.between(LocalDateTime.now(), expiryTime);
//        long durationSeconds = duration.getSeconds();
//        if (durationSeconds <= 0) {
//            return 0;
//        }
//        return durationSeconds;
        return 0;
    }


    *//**
     * 查询支付方式
     *//*
    @SneakyThrows
    private void successPaymentMethod(TradePaymentOrder order, CashierDTO dto) {
        String merchantId = order.getMerchantId();
        TradeResultDTO tradeResultDTO = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(null);
        if (Objects.isNull(tradeResultDTO)) {
            return;
        }

        MerchantResultDTO merchantResult = Optional.of(tradeResultDTO).map(TradeResultDTO::getMerchantResult)
                .orElse(new MerchantResultDTO());
        PaymentResultDTO paymentResult = Optional.of(tradeResultDTO).map(TradeResultDTO::getPaymentResult)
                .orElse(new PaymentResultDTO());
        ChannelResultDTO channelResult = Optional.of(tradeResultDTO).map(TradeResultDTO::getChannelResult)
                .orElse(new ChannelResultDTO());
        log.info("successPaymentMethod merchantResult={}", JSONUtil.toJsonStr(merchantResult));

        CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
        methodDTO.setPaymentMethod(paymentResult.getPaymentMethod());
        methodDTO.setPaymentAbbr(paymentResult.getPaymentName());
        methodDTO.setSort(1);
        methodDTO.setSingleFee(merchantResult.getSingleFee());
        methodDTO.setSingleRate(merchantResult.getSingleRate());
        dto.setOnMethod(methodDTO);

        // 可能是va、qr、paymentUrl
        String vaNumber = Optional.ofNullable(channelResult.getVaNumber()).orElse("");
        String qrString = Optional.ofNullable(channelResult.getQrString()).orElse("");
        String paymentUrl = Optional.ofNullable(channelResult.getPaymentUrl()).orElse("");
        dto.setMethodResult(vaNumber + qrString + paymentUrl);

        // 样式
        // 先查缓存
        String key = TradeConstant.CACHE_LINK_SETTING_STYLE + merchantId;
        Object obj = redisService.get(key);
        MerchantPaymentLinkSettingDTO settingDTO = Optional.ofNullable(obj).map(Object::toString)
                .map(e -> JSONUtil.toBean(e, MerchantPaymentLinkSettingDTO.class))
                .orElse(null);
        log.info("settingDTO from redis={}", JSONUtil.toJsonStr(settingDTO));

        if (Objects.isNull(settingDTO)) {
            // 再查服务
            MerchantIdParam param = new MerchantIdParam();
            param.setMerchantId(merchantId);
            settingDTO = BaseResult.parse(merchantApiService.getPaymentLinkSetting(param).toFuture().join());

            log.info("settingDTO from merchant={}", JSONUtil.toJsonStr(settingDTO));
        }

        if (Objects.nonNull(settingDTO)) {
            TradeCashierStyleDTO styleDTO = new TradeCashierStyleDTO();
            styleDTO.setLogo(settingDTO.getLogo());
            styleDTO.setBgColor(settingDTO.getBgColor());
            dto.setStyle(styleDTO);
        }
    }*/


}
