package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.SettleAccountFlow;
import com.paysphere.enums.AccountDirectionEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.query.SettleFlowQueryService;
import com.paysphere.query.dto.AccountFlowCsvDTO;
import com.paysphere.query.param.SettleAccountFlowPageParam;
import com.paysphere.repository.SettleAccountFlowService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.paysphere.TradeConstant.DF_0;


@Slf4j
@Service
public class SettleFlowQueryServiceImpl implements SettleFlowQueryService {

    @Resource
    SettleAccountFlowService accountFlowService;



    @Override
    public Page<SettleAccountFlow> pageAccountFlowList(SettleAccountFlowPageParam param) {
        log.info("pageAccountFlowList param={}", JSONUtil.toJsonStr(param));

        if (Objects.isNull(param)) {
            return new Page<>();
        }

        QueryWrapper<SettleAccountFlow> flowQuery = new QueryWrapper<>();
        flowQuery.lambda()
                .ge(StringUtils.isNotBlank(param.getStartTime()), SettleAccountFlow::getFlowTime, param.getStartTime())
                .le(StringUtils.isNotBlank(param.getEndTime()), SettleAccountFlow::getFlowTime, param.getEndTime())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleAccountFlow::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), SettleAccountFlow::getAccountNo, param.getAccountNo())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), SettleAccountFlow::getTradeNo, param.getTradeNo())
                .eq(Objects.nonNull(param.getAccountDirection()), SettleAccountFlow::getAccountDirection, param.getAccountDirection())
                .orderByDesc(SettleAccountFlow::getFlowTime);

        return accountFlowService.page(new Page<>(param.getPageNum(), param.getPageSize()), flowQuery);

    }


    @Override
    public String exportAccountFlowList(SettleAccountFlowPageParam param) {
        log.info("exportAccountFlowList param={}", JSONUtil.toJsonStr(param));

        //查询
        int limitSize = 300;
        QueryWrapper<SettleAccountFlow> flowQuery = new QueryWrapper<>();
        flowQuery.lambda()
                .ge(StringUtils.isNotBlank(param.getStartTime()), SettleAccountFlow::getFlowTime, param.getStartTime())
                .le(StringUtils.isNotBlank(param.getEndTime()), SettleAccountFlow::getFlowTime, param.getEndTime())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleAccountFlow::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), SettleAccountFlow::getAccountNo, param.getAccountNo())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), SettleAccountFlow::getTradeNo, param.getTradeNo())
                .eq(Objects.nonNull(param.getAccountDirection()), SettleAccountFlow::getAccountDirection, param.getAccountDirection())
                .last("LIMIT " + limitSize);
        List<SettleAccountFlow> payOrderList = accountFlowService.list(flowQuery);

        List<AccountFlowCsvDTO> csvDTOList = payOrderList.stream().map(e -> {
            String outerNo = Optional.of(e).map(SettleAccountFlow::getOuterNo).map(f -> "'" + f).orElse("");
            AccountFlowCsvDTO csvDTO = new AccountFlowCsvDTO();
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setMerchantNo(outerNo);
            csvDTO.setTradeType(TradeTypeEnum.tradeNoToTradeType(e.getTradeNo()).getName());
            csvDTO.setMerchantId(e.getMerchantId());
            csvDTO.setMerchantName(e.getMerchantName());
            csvDTO.setAccountNo("'" + e.getAccountNo());
            csvDTO.setAccountDirection(AccountDirectionEnum.codeToName(e.getAccountDirection()));
            csvDTO.setCurrency(e.getCurrency());
            csvDTO.setAmount(e.getAmount());
            csvDTO.setFlowTime(e.getFlowTime().format(DF_0));
            return csvDTO;
        }).toList();


        // 上传谷歌
       /* String fileName = StorageUtil.exportCsvFile("accountFlow-");
        log.info("exportAccountFlowList fileName={}", fileName);
        String uploadObject = storageHandler.uploadObject(csvDTOList, SYSTEM, fileName, "AccountFlowList");
        log.info("exportAccountFlowList uploadObject={}", uploadObject);
        return uploadObject;*/
        return null;
    }

}
