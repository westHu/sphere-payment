package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantWithdrawConfig;
import com.paysphere.query.MerchantWithdrawConfigQueryService;
import com.paysphere.query.dto.MerchantWithdrawConfigDTO;
import com.paysphere.query.param.MerchantIdParam;
import com.paysphere.repository.MerchantService;
import com.paysphere.repository.MerchantWithdrawConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantWithdrawConfigQueryServiceImpl implements MerchantWithdrawConfigQueryService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantWithdrawConfigService merchantWithdrawConfigService;


    @Override
    public MerchantWithdrawConfigDTO getMerchantWithdrawConfig(MerchantIdParam param) {
        if (Objects.isNull(param)) {
            return null;
        }

        //查询商户
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        Merchant merchant = merchantService.getOne(merchantQuery);
        if (Objects.isNull(merchant)) {
            log.error("getMerchantPayoutConfig merchant is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        //查询商户代付配置
        QueryWrapper<MerchantWithdrawConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantWithdrawConfig::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        MerchantWithdrawConfig withdrawConfig = merchantWithdrawConfigService.getOne(configQuery);
        if (Objects.isNull(withdrawConfig)) {
            log.error("getMerchantWithdrawConfig withdrawConfig is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        MerchantWithdrawConfigDTO withdrawConfigDTO = new MerchantWithdrawConfigDTO();
        withdrawConfigDTO.setMerchantId(withdrawConfig.getMerchantId());
        withdrawConfigDTO.setDeductionType(withdrawConfig.getDeductionType());
        return withdrawConfigDTO;
    }
}
