package app.sphere.query.impl;

import app.sphere.assembler.ApplicationConverter;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantConfig;
import app.sphere.query.MerchantConfigQueryService;
import app.sphere.query.dto.MerchantConfigDTO;
import app.sphere.query.dto.MerchantPaymentLinkSettingDTO;
import app.sphere.query.param.MerchantIdParam;
import domain.sphere.repository.MerchantConfigRepository;
import domain.sphere.repository.MerchantRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

import static share.sphere.TradeConstant.LIMIT_1;

/**
 * 商户配置查询服务实现类
 * 提供商户基本配置和支付链接设置的查询功能
 *
 * @author sphere
 * @since 1.0.0
 */
@Slf4j
@Service
public class MerchantConfigQueryServiceImpl implements MerchantConfigQueryService {

    @Resource
    MerchantRepository merchantRepository;
    @Resource
    MerchantConfigRepository merchantConfigRepository;
    @Resource
    ApplicationConverter applicationConverter;

    @Override
    public MerchantConfigDTO getMerchantConfig(MerchantIdParam param) {
        log.info("开始查询商户配置信息, param={}", JSONUtil.toJsonStr(param));
        Assert.notNull(param, "商户ID参数不能为空");
        Assert.hasText(param.getMerchantId(), "商户ID不能为空");

        // 1. 查询商户基本信息
        Merchant merchant = getMerchantInfo(param.getMerchantId());
        if (Objects.isNull(merchant)) {
            log.error("商户信息不存在, merchantId={}", param.getMerchantId());
            return null;
        }

        // 2. 查询商户配置信息
        MerchantConfig merchantConfig = getMerchantConfigInfo(param.getMerchantId());
        if (Objects.isNull(merchantConfig)) {
            log.error("商户配置信息不存在, merchantId={}", param.getMerchantId());
            return null;
        }

        // 3. 组装返回结果
        MerchantConfigDTO merchantConfigDTO = applicationConverter.convertMerchantConfigDTO(merchantConfig);
        merchantConfigDTO.setMerchantId(merchant.getMerchantId());
        merchantConfigDTO.setMerchantName(merchant.getMerchantName());
        
        log.info("商户配置信息查询完成, merchantId={}, merchantName={}", 
                merchant.getMerchantId(), merchant.getMerchantName());
        return merchantConfigDTO;
    }

    @Override
    public MerchantPaymentLinkSettingDTO getPaymentLinkSetting(MerchantIdParam param) {
        log.info("开始查询商户支付链接设置, param={}", JSONUtil.toJsonStr(param));
        Assert.notNull(param, "商户ID参数不能为空");
        Assert.hasText(param.getMerchantId(), "商户ID不能为空");

        // 1. 查询商户配置信息
        MerchantConfig merchantConfig = getMerchantConfigInfo(param.getMerchantId());
        if (Objects.isNull(merchantConfig)) {
            log.error("商户配置信息不存在, merchantId={}", param.getMerchantId());
            return null;
        }

        // 2. 组装返回结果
        return Optional.of(merchantConfig)
                .map(MerchantConfig::getPaymentLinkSetting)
                .map(e -> JSONUtil.toBean(e, MerchantPaymentLinkSettingDTO.class))
                .orElse(null);
    }

    //===============================================================================================================
    /**
     * 获取商户基本信息
     */
    private Merchant getMerchantInfo(String merchantId) {
        log.debug("查询商户基本信息, merchantId={}", merchantId);
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getMerchantId, merchantId)
                   .last(LIMIT_1);
        return merchantRepository.getOne(queryWrapper);
    }

    /**
     * 获取商户配置信息
     */
    private MerchantConfig getMerchantConfigInfo(String merchantId) {
        log.debug("查询商户配置信息, merchantId={}", merchantId);
        LambdaQueryWrapper<MerchantConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantConfig::getMerchantId, merchantId)
                   .last(LIMIT_1);
        return merchantConfigRepository.getOne(queryWrapper);
    }
}
