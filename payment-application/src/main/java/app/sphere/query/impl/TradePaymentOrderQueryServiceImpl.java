package app.sphere.query.impl;


import app.sphere.assembler.ApplicationConverter;
import app.sphere.command.dto.PaymentResultAttributeDTO;
import app.sphere.command.dto.TradeCallBackResultAttributeDTO;
import app.sphere.command.dto.TradeCallBackResultDTO;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.PaymentResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.query.TradePaymentOrderQueryService;
import app.sphere.query.dto.CashierDTO;
import app.sphere.query.dto.CashierPaymentMethodDTO;
import app.sphere.query.dto.MerchantPayPaymentConfigSettingDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.PayerDTO;
import app.sphere.query.dto.TradeOrderTimeLineDTO;
import app.sphere.query.dto.TradePayOrderCsvDTO;
import app.sphere.query.dto.TradePayOrderDTO;
import app.sphere.query.dto.TradePayOrderDetailDTO;
import app.sphere.query.dto.TradePayOrderPageDTO;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.TradePayOrderPageParam;
import app.sphere.query.param.TradePaymentLinkPageParam;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.TradePaymentCallBackResultRepository;
import domain.sphere.repository.TradePaymentLinkOrderRepository;
import domain.sphere.repository.TradePaymentOrderRepository;
import infrastructure.sphere.db.entity.PaymentMethod;
import infrastructure.sphere.db.entity.TradePaymentCallBackResult;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.CallBackStatusEnum;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.exception.PaymentException;
import share.sphere.utils.StorageUtil;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class TradePaymentOrderQueryServiceImpl implements TradePaymentOrderQueryService {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;
    @Resource
    TradePaymentLinkOrderRepository tradePaymentLinkOrderRepository;

    @Resource
    ApplicationConverter applicationConverter;
    @Resource
    TradePaymentCallBackResultRepository tradePaymentCallBackResultRepository;



    @Override
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
        return tradePaymentLinkOrderRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
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
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradePaymentOrder::getOrderNo, param.getOrderNo())
                .in(CollectionUtils.isNotEmpty(param.getOrderNoList()), TradePaymentOrder::getOrderNo, param.getOrderNoList());
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
        Page<TradePaymentOrder> page = tradePaymentOrderRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
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
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradePaymentOrder::getOrderNo, param.getOrderNo())
                .in(CollectionUtils.isNotEmpty(param.getOrderNoList()), TradePaymentOrder::getOrderNo, param.getOrderNoList());
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
        long count = tradePaymentOrderRepository.count(countWrapper);
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
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradePaymentOrder::getOrderNo, param.getOrderNo())
                .in(CollectionUtils.isNotEmpty(param.getOrderNoList()), TradePaymentOrder::getOrderNo, param.getOrderNoList());
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
        List<TradePaymentOrder> payOrderList = tradePaymentOrderRepository.list(queryWrapper);
        List<TradePayOrderCsvDTO> csvDTOList = payOrderList.stream().map(e -> {
            String tradeStatus = TradeStatusEnum.codeToEnum(e.getTradeStatus()).getMerchantStatus();
//            String paymentStatus = PaymentStatusEnum.codeToEnum(e.getPaymentStatus()).getMerchantStatus();
//            String tradeTime = Objects.nonNull(e.getTradeTime()) ? e.getTradeTime().format(TradeConstant.DF_0) : "";
//            String paymentFinishTime = Objects.nonNull(e.getPaymentFinishTime()) ?
//                    e.getPaymentFinishTime().format(TradeConstant.DF_0) : "";

            TradePayOrderCsvDTO csvDTO = new TradePayOrderCsvDTO();
            BeanUtils.copyProperties(e, csvDTO);
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setOrderNo("'" + e.getOrderNo());
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
        String uploadObject = null; //storageHandler.uploadObject(csvDTOList, param.getOperator(), fileName, "PayOrderList");
        log.info("exportPayOrderList uploadObject={}", uploadObject);
        return uploadObject;
    }

    /**
     * 体验收银台 & 正式收银台
     */
    @Override
    public CashierDTO getCashier(CashierParam param) {
        log.info("getCashier param={}", JSONUtil.toJsonStr(param));

        String tradeNo = param.getTradeNo();
        String timestamp = param.getTimestamp();
        String token = param.getToken();

        // 校验参数
        if (StringUtils.isNoneBlank(timestamp, token)) {
            String cashierToken = ""; //AesUtils.cashierToken(tradeNo + timestamp);
            Assert.equals(token, cashierToken, () -> new PaymentException("PAY_INVALID_TOKEN"+ tradeNo));
        }

        // 校验订单
        QueryWrapper<TradePaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException("PAY_ORDER_NOT_EXIST"+ tradeNo));

        CashierDTO dto = new CashierDTO();
        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        log.info("getCashier tradeStatusEnum={}", tradeStatusEnum.name());

        switch (tradeStatusEnum) {
            case TRADE_INIT -> initPaymentMethod(order, dto);
            case TRADE_SUCCESS -> successPaymentMethod(order, dto);
            case TRADE_FAILED -> throw new PaymentException("PAY_ORDER_HAS_FAILED"+ order.getTradeNo());
            case TRADE_EXPIRED -> throw new PaymentException("PAY_ORDER_HAS_EXPIRED"+ order.getTradeNo());
            default -> throw new PaymentException("PAY_ORDER_STATUS_INVALID"+ order.getTradeNo());
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

    /**
     * 代收订单详情
     */

    @Override
    public TradePayOrderDTO getPayOrderByTradeNo(String tradeNo) {
        log.info("getPayOrderByTradeNo param={}", tradeNo);

        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException("PAY_ORDER_NOT_EXIST"+ tradeNo));

        TradePayOrderDetailDTO detailDTO = getTradePayOrderDetailDTO(order);
        List<TradeOrderTimeLineDTO> sortLineDTOList = getTradePayOrderTimeLineList(order);

        TradePayOrderDTO payOrderDTO = new TradePayOrderDTO();
        payOrderDTO.setPayOrderDetail(detailDTO);
        payOrderDTO.setTimeLine(sortLineDTOList);
        return payOrderDTO;
    }

    /**
     * 组装订单列表
     */
    private TradePayOrderPageDTO getTradePayOrderPageDTO(TradePaymentOrder order) {
        PayerDTO payerDTO = Optional.of(order).map(TradePaymentOrder::getPayerInfo)
                .map(e -> JSONUtil.toBean(e, PayerDTO.class))
                .orElse(null);

        TradePayOrderPageDTO pageDTO = new TradePayOrderPageDTO();
        pageDTO.setPurpose(order.getPurpose());
        pageDTO.setTradeNo(order.getTradeNo());
        pageDTO.setOrderNo(order.getOrderNo());
        //pageDTO.setChannelOrderNo(channelResult.getChannelOrderNo()); // 解析
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

    /**
     * 组装订单明细
     */
    private TradePayOrderDetailDTO getTradePayOrderDetailDTO(TradePaymentOrder order) {
        PayerDTO payer = Optional.of(order).map(TradePaymentOrder::getPayerInfo)
                .map(e -> JSONUtil.toBean(e, PayerDTO.class))
                .orElse(null);

        TradePayOrderDetailDTO detailDTO = new TradePayOrderDetailDTO();
        detailDTO.setTradeNo(order.getTradeNo());
        detailDTO.setOrderNo(order.getOrderNo());
        //detailDTO.setChannelOrderNo(channelResult.getChannelOrderNo()); // 需要解析
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

    /**
     * 组装订单时间轴
     */
    @SneakyThrows
    private List<TradeOrderTimeLineDTO> getTradePayOrderTimeLineList(TradePaymentOrder order) {
        List<TradeOrderTimeLineDTO> lineDTOList = new ArrayList<>();

        // 创建订单
        TradeOrderTimeLineDTO createLine = new TradeOrderTimeLineDTO();
        createLine.setLineTime(order.getCreateTime() + "000");
        createLine.setLineMessage("Payin order receive request: SUCCESS");
        createLine.setStatus(true);
        lineDTOList.add(createLine);

        // 下单
        CompletableFuture<Void> f0 = CompletableFuture.runAsync(() -> {
            if (Objects.isNull(order.getTradeTime())) {
                return;
            }
            TradeOrderTimeLineDTO paymentLine = new TradeOrderTimeLineDTO();
            paymentLine.setLineTime(order.getTradeTime() + "000");
            TradeStatusEnum statusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());

            // 失败
            if (TradeStatusEnum.TRADE_FAILED.equals(statusEnum)) {
                paymentLine.setStatus(false);
                String message = "Payin order palace order: " + statusEnum.getMerchantStatus();
                String error = Optional.of(order).map(TradePaymentOrder::getTradeResult)
                        .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                        .map(TradeResultDTO::getError)
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
            List<TradePaymentCallBackResult> callbackResultList = tradePaymentCallBackResultRepository.list(callbackResultQuery);
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

    /**
     * 查询支付方式
     */
    private void initPaymentMethod(TradePaymentOrder order, CashierDTO dto) {
        String merchantId = order.getMerchantId();

        // 校验此收银台链接订单是否超时
        checkExpiredAgain(order);

        // 并行查询
        CompletableFuture<List<PaymentMethod>> cf0
                = CompletableFuture.supplyAsync(this::getPaymentMethodList4Transaction);
        CompletableFuture<MerchantPayPaymentConfigSettingDTO> cf1
                = CompletableFuture.supplyAsync(() -> getMerchantPayPaymentConfigList(merchantId));
        CompletableFuture.allOf(cf0, cf1).join();

        // 获取结果
        List<PaymentMethod> channelMethodDTOList;
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

    }


    /**
     * 再次确认是否超时
     */
    private void checkExpiredAgain(TradePaymentOrder order) {

    }

    /**
     * 查询商户配置的支付方式, 有缓存加持
     */
    private MerchantPayPaymentConfigSettingDTO getMerchantPayPaymentConfigList(String merchantId) {
        return null;
    }


    /**
     * 查询支付配置, 有缓存加持
     */
    @SneakyThrows
    private List<PaymentMethod> getPaymentMethodList4Transaction() {
        List<PaymentMethod> paymentMethodList = new ArrayList<>();
        // 缓存存在, 直接返回
        if (CollectionUtils.isNotEmpty(paymentMethodList)) {
            return paymentMethodList;
        } else {
            if (CollectionUtils.isEmpty(paymentMethodList)) {
                return Collections.emptyList();
            }
            return paymentMethodList;
        }
    }


    /**
     * 计算过期时间间隔（秒数）
     */
    private long getExpiryPeriod(TradePaymentOrder order) {
        return 0;
    }


    /**
     * 查询支付方式
     */
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
        log.info("successPaymentMethod merchantResult={}", JSONUtil.toJsonStr(merchantResult));

        CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
        methodDTO.setPaymentMethod(order.getPaymentMethod());
        methodDTO.setSort(1);
        methodDTO.setSingleFee(merchantResult.getSingleFee());
        methodDTO.setSingleRate(merchantResult.getSingleRate());
        dto.setOnMethod(methodDTO);

        // 可能是va、qr、paymentUrl
        String qrString = Optional.ofNullable(paymentResult.getQrString()).orElse("");
        String paymentUrl = Optional.ofNullable(paymentResult.getPaymentUrl()).orElse("");
        dto.setMethodResult(qrString + paymentUrl);

    
    }


}
