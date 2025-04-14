package app.sphere.query.impl;

import app.sphere.query.TradeTransferOrderQueryService;
import app.sphere.query.dto.TradeOrderTimeLineDTO;
import app.sphere.query.dto.TradeTransferOrderCsvDTO;
import app.sphere.query.dto.TradeTransferOrderDTO;
import app.sphere.query.dto.TradeTransferOrderDetailDTO;
import app.sphere.query.param.TradeTransferOrderPageParam;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.TradeTransferOrderRepository;
import infrastructure.sphere.db.entity.TradeTransferOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.SettleStatusEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.exception.PaymentException;
import share.sphere.utils.StorageUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class TradeTransferOrderQueryServiceImpl extends AbstractTradeOrderQueryServiceImpl implements TradeTransferOrderQueryService {

    @Resource
    TradeTransferOrderRepository tradeTransferOrderRepository;


    @Override
    public Page<TradeTransferOrder> pageTransferOrderList(TradeTransferOrderPageParam param) {
        if (Objects.isNull(param)) {
            return new Page<>();
        }
        QueryWrapper<TradeTransferOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(StringUtils.isNotBlank(param.getTradeStartTime()), TradeTransferOrder::getTradeTime, param.getTradeStartTime())
                .lt(StringUtils.isNotBlank(param.getTradeEndTime()), TradeTransferOrder::getTradeTime, param.getTradeEndTime())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeTransferOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeTransferOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), TradeTransferOrder::getAccountNo, param.getAccountNo())
                .eq(Objects.nonNull(param.getTradeStatus()), TradeTransferOrder::getTradeStatus, param.getTradeStatus())
                .orderByDesc(TradeTransferOrder::getTradeTime);
        return tradeTransferOrderRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
    }


    @Override
    public String exportTransferOrderList(TradeTransferOrderPageParam param) {
        if (Objects.isNull(param)) {
            return null;
        }

        int limitSize = 1000;

        // 查询
        QueryWrapper<TradeTransferOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(StringUtils.isNotBlank(param.getTradeStartTime()), TradeTransferOrder::getTradeTime, param.getTradeStartTime())
                .lt(StringUtils.isNotBlank(param.getTradeEndTime()), TradeTransferOrder::getTradeTime, param.getTradeEndTime())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeTransferOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeTransferOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), TradeTransferOrder::getAccountNo, param.getAccountNo())
                .eq(Objects.nonNull(param.getTradeStatus()), TradeTransferOrder::getTradeStatus, param.getTradeStatus())
                .last("LIMIT " + limitSize);
        List<TradeTransferOrder> transferOrderList = tradeTransferOrderRepository.list(queryWrapper);
        List<TradeTransferOrderCsvDTO> csvDTOList = transferOrderList.stream().map(e -> {
            TradeTransferOrderCsvDTO csvDTO = new TradeTransferOrderCsvDTO();
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setAccountNo("'" + e.getAccountNo());
            csvDTO.setTradeStatus(TradeStatusEnum.codeToEnum(e.getTradeStatus()).name());
//            csvDTO.setTradeTime(e.getTradeTime().format(TradeConstant.DF_0));
            BeanUtils.copyProperties(e, csvDTO);
            return csvDTO;
        }).toList();

        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("transfer-");
        String uploadObject = null;//'storageHandler.uploadObject(csvDTOList, param.getOperator(), fileName, "TransferOrderList");
        log.info("spherepay exportTransferOrderList uploadObject={}", uploadObject);
        return uploadObject;
    }


    @Override
    public TradeTransferOrderDTO getTransferOrderByTradeNo(String tradeNo) {
        QueryWrapper<TradeTransferOrder> orderQuery = new QueryWrapper<>();
        orderQuery.lambda().eq(TradeTransferOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeTransferOrder order = tradeTransferOrderRepository.getOne(orderQuery);
        Assert.notNull(order, () -> new PaymentException("Transfer order not exist"));

        TradeTransferOrderDetailDTO detailDTO = getTradePayOrderDetailDTO(order);
        List<TradeOrderTimeLineDTO> sortLineDTOList = getTradePayOrderTimeLineList(order);

        TradeTransferOrderDTO transferOrderDTO = new TradeTransferOrderDTO();
        transferOrderDTO.setTransferOrderDetail(detailDTO);
        transferOrderDTO.setTimeLine(sortLineDTOList);
        return transferOrderDTO;
    }


    /**
     * 组装订单明细
     */
    private TradeTransferOrderDetailDTO getTradePayOrderDetailDTO(TradeTransferOrder order) {
        TradeTransferOrderDetailDTO detailDTO = new TradeTransferOrderDetailDTO();
        detailDTO.setTradeNo(order.getTradeNo());
        detailDTO.setTradeStatus(order.getTradeStatus());
        detailDTO.setSettleStatus(Objects.isNull(order.getSettleStatus()) ?
                SettleStatusEnum.SETTLE_TODO.getCode() :
                order.getSettleStatus());

        detailDTO.setMerchantId(order.getMerchantId());
        detailDTO.setMerchantName(order.getMerchantName());
        detailDTO.setAccountNo(order.getAccountNo());
      detailDTO.setCurrency(order.getCurrency());
        detailDTO.setAmount(order.getAmount());
        detailDTO.setPurpose(order.getPurpose());
//        detailDTO.setTradeTime(order.getTradeTime());
//        detailDTO.setFinishSettleTime(order.getSettleFinishTime());
        if (order.getTradeStatus().equals(TradeStatusEnum.TRADE_FAILED.getCode())) {
            detailDTO.setFinishSettleTime(order.getUpdateTime());
        }
        return detailDTO;
    }


    /**
     * 组装订单时间轴
     */
    private List<TradeOrderTimeLineDTO> getTradePayOrderTimeLineList(TradeTransferOrder order) {
        List<TradeOrderTimeLineDTO> lineDTOList = new ArrayList<>();
        // 生成订单
        TradeOrderTimeLineDTO createLine = new TradeOrderTimeLineDTO();
//        createLine.setLineTime(order.getTradeTime());
        createLine.setLineMessage("Transfer order create: success");
        createLine.setStatus(true);
        lineDTOList.add(createLine);

        // 清结算
        SettleStatusEnum settleStatusEnum = SettleStatusEnum.codeToEnum(order.getSettleStatus());
        if (SettleStatusEnum.SETTLE_SUCCESS.equals(settleStatusEnum)
                || SettleStatusEnum.SETTLE_FAILED.equals(settleStatusEnum)) {
            TradeOrderTimeLineDTO settlementLine = new TradeOrderTimeLineDTO();
            settlementLine.setStatus(SettleStatusEnum.SETTLE_SUCCESS.equals(settleStatusEnum));
            String msg = settlementLine.isStatus() ? "success" : "failed";
//            settlementLine.setLineTime(order.getSettleFinishTime());
            settlementLine.setLineMessage("Transfer order settlement: " + msg);
            lineDTOList.add(settlementLine);
        }


        return lineDTOList.stream()
                .sorted(Comparator.comparing(TradeOrderTimeLineDTO::getLineTime))
                .toList();
    }

}
