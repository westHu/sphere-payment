package app.sphere.query.impl;

import app.sphere.query.TradeRechargeOrderQueryService;
import app.sphere.query.dto.TradeRechargeOrderCsvDTO;
import app.sphere.query.param.TradeRechargeOrderPageParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.TradeRechargeOrderRepository;
import infrastructure.sphere.db.entity.TradeRechargeOrder;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.exception.PaymentException;
import share.sphere.utils.StorageUtil;

import java.util.List;
import java.util.Objects;
@Slf4j
@Service
public class TradeRechargeOrderQueryServiceImpl implements TradeRechargeOrderQueryService {

    @Resource
    TradeRechargeOrderRepository tradeRechargeOrderRepository;


    @Override
    public Page<TradeRechargeOrder> pageRechargeOrderList(TradeRechargeOrderPageParam param) {
        if (Objects.isNull(param)) {
            return new Page<>();
        }
        QueryWrapper<TradeRechargeOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(param.getTradeStartTime()) && StringUtils.isNotBlank(param.getTradeEndTime())) {
            queryWrapper.lambda().between(TradeRechargeOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeRechargeOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeRechargeOrder::getMerchantId, param.getMerchantId())
                .eq(Objects.nonNull(param.getTradeStatus()), TradeRechargeOrder::getTradeStatus, param.getTradeStatus())
                .orderByDesc(TradeRechargeOrder::getTradeTime);
        return tradeRechargeOrderRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), queryWrapper);
    }


    @Override
    @SneakyThrows
    public String exportRechargeOrderList(TradeRechargeOrderPageParam param) {
        log.info("exportRechargeOrderList param={}", JSONUtil.toJsonStr(param));
        if (Objects.isNull(param)) {
            return null;
        }

        int limitSize = 1000;
        QueryWrapper<TradeRechargeOrder> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNoneBlank(param.getTradeStartTime(), param.getTradeEndTime())) {
            queryWrapper.lambda().between(TradeRechargeOrder::getTradeTime, param.getTradeStartTime(), param.getTradeEndTime());
        }
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeRechargeOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeRechargeOrder::getMerchantId, param.getMerchantId())
                .eq(Objects.nonNull(param.getTradeStatus()), TradeRechargeOrder::getTradeStatus, param.getTradeStatus())
                .last("LIMIT " + limitSize);
        List<TradeRechargeOrder> rechargeOrderList = tradeRechargeOrderRepository.list(queryWrapper);
        if (rechargeOrderList.size() == 0) {
            throw new PaymentException("There is no data to export, please confirm");
        }
        List<TradeRechargeOrderCsvDTO> csvDTOList = rechargeOrderList.stream().map(e -> {
            TradeRechargeOrderCsvDTO csvDTO = new TradeRechargeOrderCsvDTO();
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setPurpose(e.getPurpose());
            csvDTO.setMerchantId(e.getMerchantId());
            csvDTO.setMerchantName(e.getMerchantName());
            csvDTO.setAccountNo("'" + e.getAccountNo());
            csvDTO.setCurrency(e.getCurrency());
            csvDTO.setAmount(e.getAmount());
            csvDTO.setPaymentMethod(e.getPaymentMethod());
            csvDTO.setBankAccount(e.getBankAccount());
            csvDTO.setTradeStatus(TradeStatusEnum.codeToEnum(e.getTradeStatus()).name());
//            csvDTO.setTradeTime(e.getTradeTime().format(DF_0));
            BeanUtils.copyProperties(e, csvDTO);
            return csvDTO;
        }).toList();


        // 上传谷歌
        String fileName = StorageUtil.exportCsvFile("topUp-");
        String uploadObject = null; //storageHandler.uploadObject(csvDTOList, param.getOperator(), fileName, "RechargeOrderList");
        log.info("spherepay exportRechargeOrderList uploadObject={}", uploadObject);
        return uploadObject;
    }
}
