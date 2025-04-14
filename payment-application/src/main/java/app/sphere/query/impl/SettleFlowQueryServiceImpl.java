package app.sphere.query.impl;

import app.sphere.query.SettleFlowQueryService;
import app.sphere.query.dto.AccountFlowCsvDTO;
import app.sphere.query.param.SettleAccountFlowPageParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.SettleAccountFlowRepository;
import infrastructure.sphere.db.entity.SettleAccountFlow;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.enums.AccountDirectionEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.utils.StorageUtil;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class SettleFlowQueryServiceImpl implements SettleFlowQueryService {

    @Resource
    SettleAccountFlowRepository accountFlowService;

    @Override
    public Page<SettleAccountFlow> pageAccountFlowList(SettleAccountFlowPageParam param) {
        log.info("pageAccountFlowList param={}", JSONUtil.toJsonStr(param));

        if (Objects.isNull(param)) {
            return new Page<>();
        }

        QueryWrapper<SettleAccountFlow> flowQuery = new QueryWrapper<>();
        flowQuery.lambda()
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
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleAccountFlow::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), SettleAccountFlow::getAccountNo, param.getAccountNo())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), SettleAccountFlow::getTradeNo, param.getTradeNo())
                .eq(Objects.nonNull(param.getAccountDirection()), SettleAccountFlow::getAccountDirection, param.getAccountDirection())
                .last("LIMIT " + limitSize);
        List<SettleAccountFlow> payOrderList = accountFlowService.list(flowQuery);

        List<AccountFlowCsvDTO> csvDTOList = payOrderList.stream().map(e -> {
            AccountFlowCsvDTO csvDTO = new AccountFlowCsvDTO();
            csvDTO.setTradeNo("'" + e.getTradeNo());
            csvDTO.setMerchantNo("orderNo");
            csvDTO.setTradeType(TradeTypeEnum.tradeNoToTradeType(e.getTradeNo()).getName());
            csvDTO.setMerchantId(e.getMerchantId());
            csvDTO.setMerchantName(e.getMerchantName());
            csvDTO.setAccountNo("'" + e.getAccountNo());
            csvDTO.setAccountDirection(AccountDirectionEnum.codeToName(e.getAccountDirection()));
            csvDTO.setCurrency(e.getCurrency());
            csvDTO.setAmount(e.getAmount());
            csvDTO.setFlowTime(null);
            return csvDTO;
        }).toList();
        log.info("csvDTOList size={}", csvDTOList.size());

        // 上传
        String fileName = StorageUtil.exportCsvFile("accountFlow-");
        log.info("exportAccountFlowList fileName={}", fileName);
        String uploadObject = "";
        log.info("exportAccountFlowList uploadObject={}", uploadObject);
        return uploadObject;
    }

}
