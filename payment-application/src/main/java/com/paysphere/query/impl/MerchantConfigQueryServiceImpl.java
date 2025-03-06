package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.assembler.ApplicationConverter;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.query.MerchantConfigQueryService;
import com.paysphere.query.dto.MerchantConfigDTO;
import com.paysphere.query.dto.MerchantPaymentLinkSettingDTO;
import com.paysphere.query.param.MerchantIdParam;
import com.paysphere.repository.MerchantConfigService;
import com.paysphere.repository.MerchantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantConfigQueryServiceImpl implements MerchantConfigQueryService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantConfigService merchantConfigService;
    @Resource
    ApplicationConverter applicationConverter;


    @Override
    public MerchantConfigDTO getMerchantConfig(MerchantIdParam param) {
        log.info("getMerchantConfig param={}", JSONUtil.toJsonStr(param));

        //查询商户
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        Merchant merchant = merchantService.getOne(merchantQuery);
        if (Objects.isNull(merchant)) {
            log.error("getMerchantConfig merchant is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        //查询商户配置
        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        MerchantConfig merchantConfig = merchantConfigService.getOne(configQuery);
        if (Objects.isNull(merchantConfig)) {
            log.error("getMerchantConfig merchantConfig is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        MerchantConfigDTO merchantConfigDTO = applicationConverter.convertMerchantConfigDTO(merchantConfig);
        merchantConfigDTO.setMerchantId(merchant.getMerchantId());
        merchantConfigDTO.setMerchantCode(merchant.getMerchantCode());
        merchantConfigDTO.setMerchantName(merchant.getMerchantName());
        return merchantConfigDTO;
    }


    @Override
    public MerchantPaymentLinkSettingDTO getPaymentLinkSetting(MerchantIdParam param) {
        log.info("getPaymentLinkSetting param={}", JSONUtil.toJsonStr(param));

        //查询商户
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        Merchant merchant = merchantService.getOne(merchantQuery);
        if (Objects.isNull(merchant)) {
            log.error("getMerchantConfig merchant is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        //查询商户配置
        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.select("id, payment_link_setting as paymentLinkSetting");
        configQuery.lambda().eq(MerchantConfig::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        MerchantConfig merchantConfig = merchantConfigService.getOne(configQuery);
        if (Objects.isNull(merchantConfig)) {
            log.error("getMerchantConfig merchantConfig is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        return Optional.of(merchantConfig).map(MerchantConfig::getPaymentLinkSetting)
                .map(e -> JSONUtil.toBean(merchantConfig.getPaymentLinkSetting(), MerchantPaymentLinkSettingDTO.class))
                .orElse(null);
    }


}
