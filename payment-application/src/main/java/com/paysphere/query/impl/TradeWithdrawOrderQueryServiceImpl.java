package com.paysphere.query.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.TradeConstant;
import com.paysphere.command.dto.TradeWithdrawRemarkDTO;
import com.paysphere.command.dto.trade.result.ReviewResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.TradeTransferOrder;
import com.paysphere.db.entity.TradeWithdrawOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.query.TradeWithdrawOrderQueryService;
import com.paysphere.query.dto.TradeOrderTimeLineDTO;
import com.paysphere.query.dto.TradeWithdrawOrderCsvDTO;
import com.paysphere.query.dto.TradeWithdrawOrderDTO;
import com.paysphere.query.dto.TradeWithdrawOrderDetailDTO;
import com.paysphere.query.param.TradeWithdrawOrderPageParam;
import com.paysphere.query.param.WithdrawFlagParam;
import com.paysphere.repository.TradeTransferOrderService;
import com.paysphere.repository.TradeWithdrawOrderService;
import com.paysphere.utils.StorageUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class TradeWithdrawOrderQueryServiceImpl implements TradeWithdrawOrderQueryService {

    @Resource
    TradeWithdrawOrderService tradeWithdrawOrderService;
    @Resource
    TradeTransferOrderService tradeTransferOrderService;



    @Override
    public Page<TradeWithdrawOrder> pageWithdrawOrderList(TradeWithdrawOrderPageParam param) {
        log.info("pageWithdrawOrderList param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return new Page<>();
        }

        QueryWrapper<TradeWithdrawOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(StringUtils.isNotBlank(param.getTradeStartTime()), TradeWithdrawOrder::getTradeTime, param.getTradeStartTime())
                .lt(StringUtils.isNotBlank(param.getTradeEndTime()), TradeWithdrawOrder::getTradeTime, param.getTradeEndTime())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeWithdrawOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeWithdrawOrder::getMerchantId, param.getMerchantId())
                .eq(Objects.nonNull(param.getTradeStatus()), TradeWithdrawOrder::getTradeStatus, param.getTradeStatus())
                .orderByDesc(TradeWithdrawOrder::getTradeTime);
        return tradeWithdrawOrderService.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
    }


    @Override
    @SneakyThrows
    public String exportWithdrawOrderList(TradeWithdrawOrderPageParam param) {
        if (Objects.isNull(param)) {
            return null;
        }

        int limitSize = 3000;
        QueryWrapper<TradeWithdrawOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(StringUtils.isNotBlank(param.getTradeStartTime()), TradeWithdrawOrder::getTradeTime, param.getTradeStartTime())
                .lt(StringUtils.isNotBlank(param.getTradeEndTime()), TradeWithdrawOrder::getTradeTime, param.getTradeEndTime())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeWithdrawOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeWithdrawOrder::getMerchantId, param.getMerchantId())
                .eq(Objects.nonNull(param.getTradeStatus()), TradeWithdrawOrder::getTradeStatus, param.getTradeStatus())
                .last("LIMIT " + limitSize);
        List<TradeWithdrawOrder> withdrawOrderList = tradeWithdrawOrderService.list(queryWrapper);
        List<TradeWithdrawOrderCsvDTO> csvDTOList = withdrawOrderList.stream().map(e -> {
            TradeWithdrawOrderCsvDTO csvDTO = new TradeWithdrawOrderCsvDTO();
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setPurpose(e.getPurpose());
            csvDTO.setMerchantId(e.getMerchantId());
            csvDTO.setMerchantName(e.getMerchantName());
            csvDTO.setAccountNo("'" + e.getAccountNo());
            csvDTO.setCurrency(e.getCurrency());
            csvDTO.setAmount(e.getAmount());
            csvDTO.setPaymentMethod(e.getPaymentMethod());
            csvDTO.setWithdrawAccount(e.getWithdrawAccount());
            csvDTO.setTradeStatus(TradeStatusEnum.codeToEnum(e.getTradeStatus()).name());
//            csvDTO.setTradeTime(e.getTradeTime().format(DF_0));
            BeanUtils.copyProperties(e, csvDTO);
            return csvDTO;
        }).toList();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("withdraw-");
        String uploadObject = null;//storageHandler.uploadObject(csvDTOList, param.getOperator(), fileName, "WithdrawOrderList");
        log.info("payspherepay exportWithdrawOrderList uploadObject={}", uploadObject);
        return uploadObject;
    }

    @Override
    public TradeWithdrawOrderDTO getWithdrawOrder(String tradeNo) {
        QueryWrapper<TradeWithdrawOrder> withdrawOrderQuery = new QueryWrapper<>();
        withdrawOrderQuery.lambda().eq(TradeWithdrawOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeWithdrawOrder order = tradeWithdrawOrderService.getOne(withdrawOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.CASH_ORDER_NOT_EXIST, tradeNo));

        // 截图证据
        String proof = Optional.of(order).map(TradeWithdrawOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getReviewResult)
                .map(ReviewResultDTO::getReviewMsg)
                .map(e -> JSONUtil.toBean(e, TradeWithdrawRemarkDTO.class))
                .map(TradeWithdrawRemarkDTO::getProof)
                .orElse(null);
        log.info("getWithdrawOrder proof={}", proof);

        // 详情
        TradeWithdrawOrderDetailDTO detailDTO = new TradeWithdrawOrderDetailDTO();
        detailDTO.setTradeNo(order.getTradeNo());
        detailDTO.setPurpose(order.getPurpose());
        detailDTO.setPaymentMethod(order.getPaymentMethod());
        detailDTO.setWithdrawAccount(order.getWithdrawAccount());
        detailDTO.setMerchantId(order.getMerchantId());
        detailDTO.setMerchantName(order.getMerchantName());
        detailDTO.setAccountNo(order.getAccountNo());
        detailDTO.setCurrency(order.getCurrency());
        detailDTO.setAmount(order.getActualAmount());
        detailDTO.setActualAmount(order.getActualAmount());
        detailDTO.setAccountAmount(order.getAccountAmount());
//        detailDTO.setTradeTime(order.getTradeTime());
        detailDTO.setPaymentStatus(order.getPaymentStatus());
//        detailDTO.setSettleFinishTime(order.getSettleFinishTime());
        detailDTO.setProof(proof);

        // 时间轴
        List<TradeOrderTimeLineDTO> sortLineDTOList = new ArrayList<>();
        TradeResultDTO tradeResultDTO = Optional.of(order).map(TradeWithdrawOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .orElse(new TradeResultDTO());

        // 创建订单
        TradeOrderTimeLineDTO create = new TradeOrderTimeLineDTO();
        create.setStatus(true);
        create.setLineMessage("create withdraw order success");
        create.setLineOperator("web");
        create.setLineTime(order.getCreateTime());
        sortLineDTOList.add(create);

        // 审核
        ReviewResultDTO reviewResult = tradeResultDTO.getReviewResult();
        if (Objects.nonNull(reviewResult)) {
            TradeOrderTimeLineDTO review = new TradeOrderTimeLineDTO();
            review.setStatus(reviewResult.getReviewStatus());
            String msg = review.isStatus() ? "success" : "failed: " + reviewResult.getReviewMsg();
            review.setLineMessage("review withdraw order: " + msg);
            review.setLineOperator("system");
            review.setLineTime(reviewResult.getReviewTime());
            sortLineDTOList.add(review);
        }

        // 资金操作
        SettleStatusEnum settleStatusEnum = SettleStatusEnum.codeToEnum(order.getSettleStatus());
        if (SettleStatusEnum.SETTLE_SUCCESS.equals(settleStatusEnum)
                || SettleStatusEnum.SETTLE_FAILED.equals(settleStatusEnum)) {
            TradeOrderTimeLineDTO review = new TradeOrderTimeLineDTO();
            review.setStatus(SettleStatusEnum.SETTLE_SUCCESS.equals(settleStatusEnum));
            String msg = review.isStatus() ? "success" : "failed";
            review.setLineMessage("settle withdraw order: " + msg);
            review.setLineOperator("system");
//            review.setLineTime(order.getSettleFinishTime());
            sortLineDTOList.add(review);
        }

        TradeWithdrawOrderDTO withdrawOrderDTO = new TradeWithdrawOrderDTO();
        withdrawOrderDTO.setWithdrawOrderDetail(detailDTO);
        withdrawOrderDTO.setTimeLine(sortLineDTOList);
        return withdrawOrderDTO;
    }

    @Override
    public boolean getWithdrawFlag(WithdrawFlagParam param) {
        String beginTime = param.getWithdrawDate() + " 00:00:00";
        String endTime = param.getWithdrawDate() + " 23:59:59";
        log.info("getWithdrawFlag beginTime={}, endTime={}", beginTime, endTime);

        // 查询提现订单
        QueryWrapper<TradeWithdrawOrder> withdrawQuery = new QueryWrapper<>();
        withdrawQuery.lambda().eq(TradeWithdrawOrder::getMerchantId, param.getMerchantId())
                .between(TradeWithdrawOrder::getTradeTime, beginTime, endTime)
                .eq(TradeWithdrawOrder::getPurpose, TradeConstant.SETTLEMENT_WITHDRAW)
                .last(LIMIT_1);
        TradeWithdrawOrder withdrawOrder = tradeWithdrawOrderService.getOne(withdrawQuery);
        if (Objects.nonNull(withdrawOrder)) {
            log.info("getWithdrawFlag withdrawOrder={}", JSONUtil.toJsonStr(withdrawOrder));
            return true;
        }

        // 查询转账订单
        QueryWrapper<TradeTransferOrder> transferQuery = new QueryWrapper<>();
        transferQuery.lambda().eq(TradeTransferOrder::getMerchantId, param.getMerchantId())
                .between(TradeTransferOrder::getTradeTime, beginTime, endTime)
                .eq(TradeTransferOrder::getPurpose, TradeConstant.SETTLEMENT_TRANSFER)
                .last(LIMIT_1);
        TradeTransferOrder transferOrder = tradeTransferOrderService.getOne(transferQuery);
        if (Objects.nonNull(transferOrder)) {
            log.info("getWithdrawFlag transferOrder={}", JSONUtil.toJsonStr(transferOrder));
            return true;
        }

        return false;
    }
}
