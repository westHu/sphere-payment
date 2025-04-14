package app.sphere.query.impl;

import app.sphere.assembler.ApplicationConverter;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantPayoutConfig;
import app.sphere.query.MerchantPayoutConfigQueryService;
import app.sphere.query.dto.MerchantPayoutConfigDTO;
import app.sphere.query.param.MerchantIdParam;
import domain.sphere.repository.MerchantPayoutConfigRepository;
import domain.sphere.repository.MerchantRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

import static share.sphere.TradeConstant.LIMIT_1;

/** 商户代付配置查询服务 */
@Slf4j
@Service
public class MerchantPayoutConfigQueryServiceImpl implements MerchantPayoutConfigQueryService {

    @Resource
    MerchantRepository merchantRepository;
    @Resource
    MerchantPayoutConfigRepository merchantPayoutConfigRepository;
    @Resource
    ApplicationConverter applicationConverter;

    /** 获取商户代付配置信息 */
    @Override
    public MerchantPayoutConfigDTO getMerchantPayoutConfig(MerchantIdParam param) {
        log.info("开始查询商户代付配置信息, param={}", JSONUtil.toJsonStr(param));
        Assert.notNull(param, "商户ID参数不能为空");
        Assert.hasText(param.getMerchantId(), "商户ID不能为空");

        // 1. 查询商户基本信息
        Merchant merchant = getMerchantInfo(param.getMerchantId());
        if (Objects.isNull(merchant)) {
            log.error("商户信息不存在, merchantId={}", param.getMerchantId());
            return null;
        }

        // 2. 查询商户代付配置
        MerchantPayoutConfig payoutConfig = getPayoutConfigInfo(param.getMerchantId());
        if (Objects.isNull(payoutConfig)) {
            log.error("商户代付配置不存在, merchantId={}", param.getMerchantId());
            return null;
        }

        // 3. 转换并返回结果
        MerchantPayoutConfigDTO result = applicationConverter.convertMerchantPayoutConfigDTO(payoutConfig);
        log.info("商户代付配置查询完成, merchantId={}", param.getMerchantId());
        return result;
    }

    //=================================================================================================================

    /** 查询商户基本信息 */
    private Merchant getMerchantInfo(String merchantId) {
        log.debug("查询商户基本信息, merchantId={}", merchantId);
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getMerchantId, merchantId)
                   .last(LIMIT_1);
        return merchantRepository.getOne(queryWrapper);
    }

    /** 查询商户代付配置信息 */
    private MerchantPayoutConfig getPayoutConfigInfo(String merchantId) {
        log.debug("查询商户代付配置信息, merchantId={}", merchantId);
        LambdaQueryWrapper<MerchantPayoutConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MerchantPayoutConfig::getMerchantId, merchantId)
                   .last(LIMIT_1);
        return merchantPayoutConfigRepository.getOne(queryWrapper);
    }
}
