package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.assembler.ApplicationConverter;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantPayoutConfig;
import com.paysphere.query.MerchantPayoutConfigQueryService;
import com.paysphere.query.dto.MerchantPayoutConfigDTO;
import com.paysphere.query.param.MerchantIdParam;
import com.paysphere.repository.MerchantPayoutConfigService;
import com.paysphere.repository.MerchantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantPayoutConfigQueryServiceImpl implements MerchantPayoutConfigQueryService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantPayoutConfigService merchantPayoutConfigService;
    @Resource
    ApplicationConverter applicationConverter;


    @Override
    public MerchantPayoutConfigDTO getMerchantPayoutConfig(MerchantIdParam param) {
        log.info("getMerchantPayoutConfig param={}", JSONUtil.toJsonStr(param));

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
        QueryWrapper<MerchantPayoutConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantPayoutConfig::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        MerchantPayoutConfig cashConfig = merchantPayoutConfigService.getOne(configQuery);
        if (Objects.isNull(cashConfig)) {
            log.error("getMerchantPayoutConfig getMerchantPayoutConfig is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        MerchantPayoutConfigDTO cashConfigDTO = applicationConverter.convertMerchantPayoutConfigDTO(cashConfig);
        cashConfigDTO.setMerchantCode(merchant.getMerchantCode());
        return cashConfigDTO;
    }
}
