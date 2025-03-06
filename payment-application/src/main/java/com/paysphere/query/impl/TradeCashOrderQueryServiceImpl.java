package com.paysphere.query.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.dto.PaymentResultAttributeDTO;
import com.paysphere.command.dto.TradeCallBackResultAttributeDTO;
import com.paysphere.command.dto.TradeCallBackResultDTO;
import com.paysphere.command.dto.trade.result.ChannelResultDTO;
import com.paysphere.command.dto.trade.result.MerchantResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.TradePayoutCallBackResult;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.enums.CallBackStatusEnum;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.query.TradeCashOrderQueryService;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.TradeCashOrderCsvDTO;
import com.paysphere.query.dto.TradeCashOrderDTO;
import com.paysphere.query.dto.TradeCashOrderDetailDTO;
import com.paysphere.query.dto.TradeCashOrderPageDTO;
import com.paysphere.query.dto.TradeCashReceiptDTO;
import com.paysphere.query.dto.TradeOrderTimeLineDTO;
import com.paysphere.query.param.TradeCashOrderPageParam;
import com.paysphere.repository.TradePayoutCallBackResultService;
import com.paysphere.repository.TradePayoutOrderService;
import com.paysphere.utils.StorageUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class TradeCashOrderQueryServiceImpl extends AbstractTradeOrderQueryServiceImpl implements TradeCashOrderQueryService {

    @Resource
    TradePayoutOrderService tradePayoutOrderService;
    @Resource
    TradePayoutCallBackResultService tradePayoutCallBackResultService;

    @Resource
    RedisService redisService;



    @Override
    public PageDTO<TradeCashOrderPageDTO> pageCashOrderList(TradeCashOrderPageParam param) {
        if (Objects.isNull(param)) {
            return PageDTO.empty();
        }
        if (Objects.nonNull(param.getAmountMin()) || Objects.nonNull(param.getAmountMax()) 
                && (StringUtils.isBlank(param.getMerchantId()))) {
                throw new PaymentException("If query by amount. Please select the merchant ID first.");
            
        }
        QueryWrapper<TradePayoutOrder> queryWrapper = new QueryWrapper<>();
        // 单号索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradePayoutOrder::getTradeNo, param.getTradeNo())
                .in(CollectionUtils.isNotEmpty(param.getTradeNoList()), TradePayoutOrder::getTradeNo, param.getTradeNoList())
                .eq(StringUtils.isNotBlank(param.getOuterNo()), TradePayoutOrder::getOuterNo, param.getOuterNo())
                .in(CollectionUtils.isNotEmpty(param.getOuterNoList()), TradePayoutOrder::getOuterNo, param.getOuterNoList());
        // 时间商户索引
        if (StringUtils.isNoneBlank(param.getTradeStartTime(), param.getTradeEndTime())) {
            queryWrapper.lambda().between(TradePayoutOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        if (StringUtils.isNoneBlank(param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime())) {
            queryWrapper.lambda().between(TradePayoutOrder::getPaymentFinishTime, param.getPaymentFinishStartTime(),
                    param.getPaymentFinishEndTime());
        }
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradePayoutOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getMerchantName()), TradePayoutOrder::getMerchantName, param.getMerchantName());
        // 非索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradePayoutOrder::getPaymentMethod, param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelName()), TradePayoutOrder::getChannelCode, param.getChannelName())
                .ge(Objects.nonNull(param.getAmountMin()), TradePayoutOrder::getAmount, param.getAmountMin())
                .le(Objects.nonNull(param.getAmountMax()), TradePayoutOrder::getAmount, param.getAmountMax())
                .eq(StringUtils.isNotBlank(param.getCashAccount()), TradePayoutOrder::getCashAccount, param.getCashAccount())
                .eq(Objects.nonNull(param.getTradeStatus()), TradePayoutOrder::getTradeStatus, param.getTradeStatus())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePayoutOrder::getPaymentStatus, param.getPaymentStatus())
                .eq(Objects.nonNull(param.getCallBackStatus()), TradePayoutOrder::getCallBackStatus, param.getCallBackStatus());

        // 排序
        queryWrapper.lambda().orderByDesc(TradePayoutOrder::getTradeTime);
        Page<TradePayoutOrder> page = tradePayoutOrderService.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
        if (page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<TradeCashOrderPageDTO> collect = page.getRecords().stream().map(this::getTradeCashOrderPageDTO).toList();
        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);

    }


    @Override
    @SneakyThrows
    public String exportCashOrderList(TradeCashOrderPageParam param) {
        if (Objects.isNull(param)) {
            return null;
        }

        int limitSize = 5000;

        // 统计数量
        QueryWrapper<TradePayoutOrder> countWrapper = new QueryWrapper<>();
        // 单号索引
        countWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradePayoutOrder::getTradeNo, param.getTradeNo())
                .in(CollectionUtils.isNotEmpty(param.getTradeNoList()), TradePayoutOrder::getTradeNo, param.getTradeNoList())
                .eq(StringUtils.isNotBlank(param.getOuterNo()), TradePayoutOrder::getOuterNo, param.getOuterNo())
                .in(CollectionUtils.isNotEmpty(param.getOuterNoList()), TradePayoutOrder::getOuterNo, param.getOuterNoList());
        // 时间商户索引
        if (StringUtils.isNoneBlank(param.getTradeStartTime(), param.getTradeEndTime())) {
            countWrapper.lambda().between(TradePayoutOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        if (StringUtils.isNoneBlank(param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime())) {
            countWrapper.lambda().between(TradePayoutOrder::getPaymentFinishTime, param.getPaymentFinishStartTime(),
                    param.getPaymentFinishEndTime());
        }
        countWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradePayoutOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getMerchantName()), TradePayoutOrder::getMerchantName, param.getMerchantName());
        // 非索引
        countWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradePayoutOrder::getPaymentMethod, param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelName()), TradePayoutOrder::getChannelCode, param.getChannelName())
                .ge(Objects.nonNull(param.getAmountMin()), TradePayoutOrder::getAmount, param.getAmountMin())
                .le(Objects.nonNull(param.getAmountMax()), TradePayoutOrder::getAmount, param.getAmountMax())
                .eq(StringUtils.isNotBlank(param.getCashAccount()), TradePayoutOrder::getCashAccount, param.getCashAccount())
                .eq(Objects.nonNull(param.getTradeStatus()), TradePayoutOrder::getTradeStatus, param.getTradeStatus())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePayoutOrder::getPaymentStatus, param.getPaymentStatus())
                .eq(Objects.nonNull(param.getCallBackStatus()), TradePayoutOrder::getCallBackStatus, param.getCallBackStatus());
        long count = tradePayoutOrderService.count(countWrapper);
        log.info("exportCashOrderList count={}", count);
        if (count == 0) {
            throw new PaymentException("There is no data to export, please confirm");
        }
        if (count > limitSize) {
            throw new PaymentException("The amount of payout data exported is too large, please contact our supporters");
        }


        // 导出查询
        QueryWrapper<TradePayoutOrder> queryWrapper = new QueryWrapper<>();
        // 单号索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradePayoutOrder::getTradeNo, param.getTradeNo())
                .in(CollectionUtils.isNotEmpty(param.getTradeNoList()), TradePayoutOrder::getTradeNo, param.getTradeNoList())
                .eq(StringUtils.isNotBlank(param.getOuterNo()), TradePayoutOrder::getOuterNo, param.getOuterNo())
                .in(CollectionUtils.isNotEmpty(param.getOuterNoList()), TradePayoutOrder::getOuterNo, param.getOuterNoList());
        // 时间商户索引
        if (StringUtils.isNoneBlank(param.getTradeStartTime(), param.getTradeEndTime())) {
            queryWrapper.lambda().between(TradePayoutOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        if (StringUtils.isNoneBlank(param.getPaymentFinishStartTime(), param.getPaymentFinishEndTime())) {
            queryWrapper.lambda().between(TradePayoutOrder::getPaymentFinishTime, param.getPaymentFinishStartTime(),
                    param.getPaymentFinishEndTime());
        }
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradePayoutOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getMerchantName()), TradePayoutOrder::getMerchantName, param.getMerchantName());
        // 非索引
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getPaymentMethod()), TradePayoutOrder::getPaymentMethod, param.getPaymentMethod())
                .eq(StringUtils.isNotBlank(param.getChannelName()), TradePayoutOrder::getChannelCode, param.getChannelName())
                .ge(Objects.nonNull(param.getAmountMin()), TradePayoutOrder::getAmount, param.getAmountMin())
                .le(Objects.nonNull(param.getAmountMax()), TradePayoutOrder::getAmount, param.getAmountMax())
                .eq(StringUtils.isNotBlank(param.getCashAccount()), TradePayoutOrder::getCashAccount, param.getCashAccount())
                .eq(Objects.nonNull(param.getTradeStatus()), TradePayoutOrder::getTradeStatus, param.getTradeStatus())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePayoutOrder::getPaymentStatus, param.getPaymentStatus())
                .eq(Objects.nonNull(param.getCallBackStatus()), TradePayoutOrder::getCallBackStatus, param.getCallBackStatus());
        List<TradePayoutOrder> cashOrderList = tradePayoutOrderService.list(queryWrapper);

        List<TradeCashOrderCsvDTO> csvDTOList = cashOrderList.stream().map(e -> {
            String tradeStatus = TradeStatusEnum.codeToEnum(e.getTradeStatus()).getMerchantStatus();
//            String paymentStatus = PaymentStatusEnum.codeToEnum(e.getPaymentStatus()).getMerchantStatus();
//            String tradeTime = Objects.nonNull(e.getTradeTime()) ? e.getTradeTime().format(TradeConstant.DF_0) : "";
//            String paymentFinishTime = Objects.nonNull(e.getPaymentFinishTime()) ? e.getPaymentFinishTime().format(TradeConstant.DF_0) : "";

            TradeCashOrderCsvDTO csvDTO = new TradeCashOrderCsvDTO();
            BeanUtils.copyProperties(e, csvDTO);
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setOuterNo("'" + e.getOuterNo());
            csvDTO.setAccountNo("'" + e.getAccountNo());
            csvDTO.setTradeStatus(tradeStatus);
//            csvDTO.setPaymentStatus(paymentStatus);
//            csvDTO.setTradeTime(tradeTime);
//            csvDTO.setPaymentFinishTime(paymentFinishTime);
            csvDTO.setMerchantFee(e.getMerchantFee());
            return csvDTO;
        }).toList();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("payout-");
        String uploadObject = null;//storageHandler.uploadObject(csvDTOList, param.getOperator(), fileName, "CashOrderList");
        log.info("exportCashOrderList uploadObject={}", uploadObject);
        return uploadObject;
    }


    @Override
    public TradeCashOrderDTO getCashOrderByTradeNo(String tradeNo) {
        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderService.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.CASH_ORDER_NOT_EXIST, tradeNo));


        TradeCashOrderDetailDTO detailDTO = getTradeCashOrderDetailDTO(order);
        List<TradeOrderTimeLineDTO> sortLineDTOList = getTradeCashOrderTimeLineList(order);

        TradeCashOrderDTO payOrderDTO = new TradeCashOrderDTO();
        payOrderDTO.setCashOrderDetail(detailDTO);
        payOrderDTO.setTimeLine(sortLineDTOList);
        return payOrderDTO;
    }


    @Override
    public TradeCashReceiptDTO getCashReceipt(String tradeNo) {
        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.select("id, " +
                "trade_no as tradeNo, " +
                "trade_time as tradeTime, " +
                "payment_finish_time as paymentFinishTime, " +
                "payment_status as paymentStatus, " +
                "merchant_id as merchantId, " +
                "merchant_name as merchantName, " +
                "cash_account as cashAccount, " +
                "currency as currency, " +
                "amount as amount ");
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePayoutOrder order = tradePayoutOrderService.getOne(cashOrderQuery);
        if (Objects.isNull(order)) {
            return null;
        }

        TradeCashReceiptDTO dto = new TradeCashReceiptDTO();
        dto.setTradeNo(order.getTradeNo());
//        dto.setTradeTime(order.getTradeTime());
//        dto.setPaymentFinishTime(order.getPaymentFinishTime());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setMerchantId(order.getMerchantId());
        dto.setMerchantName(order.getMerchantName());
        dto.setCashAccount(order.getCashAccount());
        dto.setCurrency(order.getCurrency());
        dto.setAmount(order.getAmount());
        dto.setPurpose(order.getPurpose());
        return dto;
    }


    // --------------------------------------------------------------------------------------------------

    /**
     * 组装订单列表
     */
    private TradeCashOrderPageDTO getTradeCashOrderPageDTO(TradePayoutOrder order) {
        ChannelResultDTO channelResult = parseChannelResult(order.getTradeResult());

        TradeCashOrderPageDTO pageDTO = new TradeCashOrderPageDTO();
        pageDTO.setPurpose(order.getPurpose());
        pageDTO.setTradeNo(order.getTradeNo());
        pageDTO.setOuterNo(order.getOuterNo());
        pageDTO.setChannelOrderNo(channelResult.getChannelOrderNo());
        pageDTO.setPaymentMethod(order.getPaymentMethod());
        pageDTO.setChannelName(order.getChannelCode());

        pageDTO.setMerchantId(order.getMerchantId());
        pageDTO.setMerchantName(order.getMerchantName());
        pageDTO.setAmount(order.getAmount());
        pageDTO.setMerchantFee(order.getMerchantFee());
        pageDTO.setActualAmount(order.getActualAmount());
        pageDTO.setAccountAmount(order.getAccountAmount());
        pageDTO.setTradeStatus(order.getTradeStatus());
        pageDTO.setPaymentStatus(order.getPaymentStatus());

        pageDTO.setCallBackStatus(order.getCallBackStatus());
//        pageDTO.setTradeTime(order.getTradeTime());
//        pageDTO.setPaymentFinishTime(order.getPaymentFinishTime());
        pageDTO.setSource(order.getSource());
        return pageDTO;
    }


    /**
     * 组装订单明细
     */
    private TradeCashOrderDetailDTO getTradeCashOrderDetailDTO(TradePayoutOrder order) {
        String receiverInfo = order.getReceiverInfo();


        MerchantResultDTO merchantResult = parseMerchantResult(order.getTradeResult());
        ChannelResultDTO channelResult = parseChannelResult(order.getTradeResult());

        TradeCashOrderDetailDTO detailDTO = new TradeCashOrderDetailDTO();
        detailDTO.setTradeNo(order.getTradeNo());
        detailDTO.setOuterNo(order.getOuterNo());
        detailDTO.setChannelOrderNo(channelResult.getChannelOrderNo()); // 需要解析
        detailDTO.setMerchantId(order.getMerchantId());
        detailDTO.setMerchantName(order.getMerchantName());
        detailDTO.setAccountNo(order.getAccountNo());
        detailDTO.setDeductionType(merchantResult.getDeductionType());
        detailDTO.setCurrency(order.getCurrency());
        detailDTO.setAmount(order.getAmount());
        detailDTO.setActualAmount(order.getActualAmount());
        detailDTO.setMerchantFee(order.getMerchantFee());
        detailDTO.setChannelCost(order.getChannelCost());
        detailDTO.setAccountAmount(order.getAccountAmount());
        detailDTO.setPaymentMethod(order.getPaymentMethod());
        detailDTO.setChannelCode(order.getChannelCode());
        detailDTO.setChannelName(order.getChannelCode());
//        detailDTO.setCashName(StringUtils.isNotBlank(receiverName) ? receiverName : order.getMerchantName());
//        detailDTO.setCashPhone(receiverParam.getPhone());
//        detailDTO.setCashEmail(receiverParam.getEmail());
        detailDTO.setCashBank(order.getPaymentMethod());
        detailDTO.setCashBankCode(order.getPaymentMethod());
        detailDTO.setCashAccount(order.getCashAccount());
        detailDTO.setPurpose(order.getPurpose());
//        detailDTO.setTradeTime(order.getTradeTime());
//        detailDTO.setPaymentFinishTime(order.getPaymentFinishTime());

        detailDTO.setPaymentStatus(Objects.isNull(order.getPaymentStatus()) ?
                PaymentStatusEnum.PAYMENT_PROCESSING.getCode() :
                order.getPaymentStatus());
        detailDTO.setCallBackStatus(Objects.isNull(order.getCallBackStatus()) ?
                CallBackStatusEnum.CALLBACK_TODO.getCode() :
                order.getCallBackStatus());
        detailDTO.setSettleStatus(Objects.isNull(order.getSettleStatus()) ?
                SettleStatusEnum.SETTLE_TODO.getCode() :
                order.getSettleStatus());
        return detailDTO;
    }

    /**
     * 组装订单时间轴
     */
    @SneakyThrows
    private List<TradeOrderTimeLineDTO> getTradeCashOrderTimeLineList(TradePayoutOrder order) {
        List<TradeOrderTimeLineDTO> lineDTOList = new ArrayList<>();

        // 创建订单
        TradeOrderTimeLineDTO createLine = new TradeOrderTimeLineDTO();
        createLine.setLineTime(order.getCreateTime());
        createLine.setLineMessage("Payout order receive request result: SUCCESS");
        createLine.setStatus(true);
        lineDTOList.add(createLine);


        // 下单
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            if (Objects.isNull(order.getTradeTime())) {
                return;
            }
            TradeOrderTimeLineDTO paymentLine = new TradeOrderTimeLineDTO();
//            paymentLine.setLineTime(order.getTradeTime());
            TradeStatusEnum statusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());

            // 失败
            if (TradeStatusEnum.TRADE_FAILED.equals(statusEnum)) {
                paymentLine.setStatus(false);
                String message = "Payout order place order result: " + statusEnum.getMerchantStatus();
                String error = Optional.of(order).map(TradePayoutOrder::getTradeResult)
                        .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                        .map(TradeResultDTO::getError)
                        .map(this::getCashErrorMsg)
                        .orElse("");

                message = message + " | error: " + error;
                paymentLine.setLineMessage(message);
            }

            // 成功
            if (TradeStatusEnum.TRADE_SUCCESS.equals(statusEnum)) {
                paymentLine.setStatus(true);
                String message = "Payout order transaction result: " + statusEnum.getMerchantStatus();
                paymentLine.setLineMessage(message);
            }

            // 待审核
            if (TradeStatusEnum.TRADE_REVIEW.equals(statusEnum)) {
                paymentLine.setStatus(true);
                String message = "Payout order transaction result: Pending " + statusEnum.getMerchantStatus();
                paymentLine.setLineMessage(message);
            }
            lineDTOList.add(paymentLine);
        });


        // 支付
        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
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
                    new StringBuilder("Payout order paid result: ").append(statusEnum.getName());//???
            if (StringUtils.isNotBlank(type)) {
                message.append(". Operator type : ").append(type);
            }
            TradeOrderTimeLineDTO paymentLine = new TradeOrderTimeLineDTO();
//            paymentLine.setLineTime(order.getPaymentFinishTime());
//            paymentLine.setLineMessage(message.toString());
            paymentLine.setStatus(PaymentStatusEnum.PAYMENT_SUCCESS.equals(statusEnum));
            paymentLine.setLineOperator(operator);
            lineDTOList.add(paymentLine);
        });

        // 回调
        CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> {
            QueryWrapper<TradePayoutCallBackResult> callbackResultQuery = new QueryWrapper<>();
            callbackResultQuery.lambda().eq(TradePayoutCallBackResult::getTradeNo, order.getTradeNo());
            List<TradePayoutCallBackResult> callbackResultList = tradePayoutCallBackResultService.list(callbackResultQuery);
            if (CollectionUtils.isNotEmpty(callbackResultList)) {
                List<TradeOrderTimeLineDTO> collect = callbackResultList.stream().map(e -> {
                    String operator = Optional.ofNullable(e.getAttribute())
                            .map(f -> JSONUtil.toBean(f, TradeCallBackResultAttributeDTO.class))
                            .map(TradeCallBackResultAttributeDTO::getOperator)
                            .orElse(TradeConstant.SYSTEM);
                    String message = Optional.ofNullable(e.getCallBackResult())
                            .map(f -> JSONUtil.toBean(f, TradeCallBackResultDTO.class))
                            .map(TradeCallBackResultDTO::getMessage)
                            .orElse("-");

                    TradeOrderTimeLineDTO callbackLine = new TradeOrderTimeLineDTO();
//                    callbackLine.setLineTime(e.getCallBackTime());
                    callbackLine.setLineMessage("Payout order callback merchant result: " + message);
                    callbackLine.setStatus(CallBackStatusEnum.CALLBACK_SUCCESS.getCode().equals(e.getCallBackStatus()));
                    callbackLine.setLineOperator(operator);
                    return callbackLine;
                }).toList();
                lineDTOList.addAll(collect);
            }
        });

        // 清结算
        CompletableFuture<Void> f4 = CompletableFuture.runAsync(() -> {
            if (Objects.isNull(order.getSettleStatus()) || Objects.isNull(order.getSettleFinishTime())
                    || 0 == order.getSettleStatus()) {
                return;
            }

            PaymentResultAttributeDTO dto = Optional.ofNullable(order.getAttribute())
                    .map(e -> JSONUtil.toBean(e, PaymentResultAttributeDTO.class))
                    .orElse(new PaymentResultAttributeDTO());
            String type = dto.getType();
            String operator = dto.getOperator();
            StringBuilder message = new StringBuilder("Payout order settlement complete");
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

        CompletableFuture.allOf(f1, f2, f3, f4).join();
        log.info("lineDTOList={}", JSONUtil.toJsonStr(lineDTOList));

        return lineDTOList.stream()
                .sorted(Comparator.comparing(TradeOrderTimeLineDTO::getLineTime))
                .toList();
    }


}
