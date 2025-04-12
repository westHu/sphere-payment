package app.sphere.query.impl;

import app.sphere.command.dto.PaymentResultAttributeDTO;
import app.sphere.command.dto.TradeCallBackResultAttributeDTO;
import app.sphere.command.dto.TradeCallBackResultDTO;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.PaymentResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.query.TradePayoutOrderQueryService;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.TradeCashOrderCsvDTO;
import app.sphere.query.dto.TradeCashOrderDTO;
import app.sphere.query.dto.TradeCashOrderDetailDTO;
import app.sphere.query.dto.TradeCashOrderPageDTO;
import app.sphere.query.dto.TradeCashReceiptDTO;
import app.sphere.query.dto.TradeOrderTimeLineDTO;
import app.sphere.query.param.TradeCashOrderPageParam;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.TradePayoutCallBackResultRepository;
import domain.sphere.repository.TradePayoutOrderRepository;
import infrastructure.sphere.db.entity.TradePayoutCallBackResult;
import infrastructure.sphere.db.entity.TradePayoutOrder;
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
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import share.sphere.utils.StorageUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class TradePayoutOrderQueryServiceImpl extends AbstractTradeOrderQueryServiceImpl implements TradePayoutOrderQueryService {

    @Resource
    TradePayoutOrderRepository tradePayoutOrderRepository;
    @Resource
    TradePayoutCallBackResultRepository tradePayoutCallBackResultRepository;



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
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradePayoutOrder::getOrderNo, param.getOrderNo())
                .in(CollectionUtils.isNotEmpty(param.getOrderNoList()), TradePayoutOrder::getOrderNo, param.getOrderNoList());
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
                .eq(Objects.nonNull(param.getTradeStatus()), TradePayoutOrder::getTradeStatus, param.getTradeStatus())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePayoutOrder::getPaymentStatus, param.getPaymentStatus())
                .eq(Objects.nonNull(param.getCallBackStatus()), TradePayoutOrder::getCallBackStatus, param.getCallBackStatus());

        // 排序
        queryWrapper.lambda().orderByDesc(TradePayoutOrder::getTradeTime);
        Page<TradePayoutOrder> page = tradePayoutOrderRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
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
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradePayoutOrder::getOrderNo, param.getOrderNo())
                .in(CollectionUtils.isNotEmpty(param.getOrderNoList()), TradePayoutOrder::getOrderNo, param.getOrderNoList());
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
                .eq(Objects.nonNull(param.getTradeStatus()), TradePayoutOrder::getTradeStatus, param.getTradeStatus())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePayoutOrder::getPaymentStatus, param.getPaymentStatus())
                .eq(Objects.nonNull(param.getCallBackStatus()), TradePayoutOrder::getCallBackStatus, param.getCallBackStatus());
        long count = tradePayoutOrderRepository.count(countWrapper);
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
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradePayoutOrder::getOrderNo, param.getOrderNo())
                .in(CollectionUtils.isNotEmpty(param.getOrderNoList()), TradePayoutOrder::getOrderNo, param.getOrderNoList());
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
                .eq(Objects.nonNull(param.getTradeStatus()), TradePayoutOrder::getTradeStatus, param.getTradeStatus())
                .eq(Objects.nonNull(param.getPaymentStatus()), TradePayoutOrder::getPaymentStatus, param.getPaymentStatus())
                .eq(Objects.nonNull(param.getCallBackStatus()), TradePayoutOrder::getCallBackStatus, param.getCallBackStatus());
        List<TradePayoutOrder> cashOrderList = tradePayoutOrderRepository.list(queryWrapper);

        List<TradeCashOrderCsvDTO> csvDTOList = cashOrderList.stream().map(e -> {
            String tradeStatus = TradeStatusEnum.codeToEnum(e.getTradeStatus()).getMerchantStatus();
//            String paymentStatus = PaymentStatusEnum.codeToEnum(e.getPaymentStatus()).getMerchantStatus();
//            String tradeTime = Objects.nonNull(e.getTradeTime()) ? e.getTradeTime().format(TradeConstant.DF_0) : "";
//            String paymentFinishTime = Objects.nonNull(e.getPaymentFinishTime()) ? e.getPaymentFinishTime().format(TradeConstant.DF_0) : "";

            TradeCashOrderCsvDTO csvDTO = new TradeCashOrderCsvDTO();
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
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo));


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
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(cashOrderQuery);
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
        PaymentResultDTO paymentResultDTO = parsePaymentResult(order.getTradeResult());

        TradeCashOrderPageDTO pageDTO = new TradeCashOrderPageDTO();
        pageDTO.setPurpose(order.getPurpose());
        pageDTO.setTradeNo(order.getTradeNo());
        pageDTO.setOrderNo(order.getOrderNo());
        pageDTO.setChannelOrderNo(paymentResultDTO.getChannelOrderNo());
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
        PaymentResultDTO paymentResultDTO = parsePaymentResult(order.getTradeResult());

        TradeCashOrderDetailDTO detailDTO = new TradeCashOrderDetailDTO();
        detailDTO.setTradeNo(order.getTradeNo());
        detailDTO.setOrderNo(order.getOrderNo());
        detailDTO.setChannelOrderNo(paymentResultDTO.getChannelOrderNo()); // 需要解析
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
        createLine.setLineTime(order.getCreateTime() + "");
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
                        .map(this::getErrorMsg)
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
            List<TradePayoutCallBackResult> callbackResultList = tradePayoutCallBackResultRepository.list(callbackResultQuery);
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
