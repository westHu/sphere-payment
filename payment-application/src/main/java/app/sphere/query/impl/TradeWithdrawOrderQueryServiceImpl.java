package app.sphere.query.impl;

import app.sphere.command.dto.TradeWithdrawRemarkDTO;
import app.sphere.command.dto.trade.result.ReviewResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.query.TradeWithdrawOrderQueryService;
import app.sphere.query.dto.TradeOrderTimeLineDTO;
import app.sphere.query.dto.TradeWithdrawOrderCsvDTO;
import app.sphere.query.dto.TradeWithdrawOrderDTO;
import app.sphere.query.dto.TradeWithdrawOrderDetailDTO;
import app.sphere.query.param.TradeWithdrawOrderPageParam;
import app.sphere.query.param.WithdrawFlagParam;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.TradeTransferOrderRepository;
import domain.sphere.repository.TradeWithdrawOrderRepository;
import infrastructure.sphere.db.entity.TradeTransferOrder;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import share.sphere.utils.StorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class TradeWithdrawOrderQueryServiceImpl implements TradeWithdrawOrderQueryService {

    @Resource
    TradeWithdrawOrderRepository tradeWithdrawOrderRepository;
    @Resource
    TradeTransferOrderRepository tradeTransferOrderRepository;



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
        return tradeWithdrawOrderRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
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
        List<TradeWithdrawOrder> withdrawOrderList = tradeWithdrawOrderRepository.list(queryWrapper);
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
            csvDTO.setWithdrawAccount(e.getBankAccount());
            csvDTO.setTradeStatus(TradeStatusEnum.codeToEnum(e.getTradeStatus()).name());
//            csvDTO.setTradeTime(e.getTradeTime().format(DF_0));
            BeanUtils.copyProperties(e, csvDTO);
            return csvDTO;
        }).toList();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("withdraw-");
        String uploadObject = null;//storageHandler.uploadObject(csvDTOList, param.getOperator(), fileName, "WithdrawOrderList");
        log.info("spherepay exportWithdrawOrderList uploadObject={}", uploadObject);
        return uploadObject;
    }

    @Override
    public TradeWithdrawOrderDTO getWithdrawOrder(String tradeNo) {
        QueryWrapper<TradeWithdrawOrder> withdrawOrderQuery = new QueryWrapper<>();
        withdrawOrderQuery.lambda().eq(TradeWithdrawOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeWithdrawOrder order = tradeWithdrawOrderRepository.getOne(withdrawOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, tradeNo));

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
        detailDTO.setWithdrawAccount(order.getBankAccount());
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
        create.setLineTime(order.getCreateTime() + "");
        sortLineDTOList.add(create);

        // 审核
        ReviewResultDTO reviewResult = tradeResultDTO.getReviewResult();
        if (Objects.nonNull(reviewResult)) {
            TradeOrderTimeLineDTO review = new TradeOrderTimeLineDTO();
            review.setStatus(reviewResult.getReviewStatus());
            String msg = review.isStatus() ? "success" : "failed: " + reviewResult.getReviewMsg();
            review.setLineMessage("review withdraw order: " + msg);
            review.setLineOperator("system");
            review.setLineTime(reviewResult.getReviewTime() + "");
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
        TradeWithdrawOrder withdrawOrder = tradeWithdrawOrderRepository.getOne(withdrawQuery);
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
        TradeTransferOrder transferOrder = tradeTransferOrderRepository.getOne(transferQuery);
        if (Objects.nonNull(transferOrder)) {
            log.info("getWithdrawFlag transferOrder={}", JSONUtil.toJsonStr(transferOrder));
            return true;
        }

        return false;
    }
}
