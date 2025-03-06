package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.MerchantConfigCmdService;
import com.paysphere.command.cmd.MerchantConfigUpdateCmd;
import com.paysphere.command.cmd.PaymentLinkSettingCmd;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.MerchantConfigService;
import com.paysphere.repository.MerchantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MerchantConfigCmdServiceImpl implements MerchantConfigCmdService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantConfigService merchantConfigService;

    /**
     * 更新商户配置
     */
    @Override
    public boolean updateMerchantConfig(MerchantConfigUpdateCmd cmd) {
        log.info("updateMerchantConfig cmd={}", JSONUtil.toJsonStr(cmd));

        String merchantId = cmd.getMerchantId();
        String finishCashUrl = cmd.getFinishCashUrl();
        String finishPaymentUrl = cmd.getFinishPaymentUrl();
        String finishRefundUrl = cmd.getFinishRefundUrl();
        String finishRedirectUrl = cmd.getFinishRedirectUrl();
        String ipWhiteList = cmd.getIpWhiteList();


        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, merchantId);
        Merchant merchant = merchantService.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_EXIST, merchantId));

        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, merchantId);
        MerchantConfig merchantConfig = merchantConfigService.getOne(configQuery);
        Assert.notNull(merchantConfig, () -> new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, merchantId));

        UpdateWrapper<MerchantConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(StringUtils.isNotBlank(finishPaymentUrl), MerchantConfig::getFinishPaymentUrl, finishPaymentUrl)
                .set(StringUtils.isNotBlank(finishCashUrl), MerchantConfig::getFinishCashUrl, finishCashUrl)
                .set(StringUtils.isNotBlank(finishRefundUrl), MerchantConfig::getFinishRefundUrl, finishRefundUrl)
                .set(StringUtils.isNotBlank(finishRedirectUrl), MerchantConfig::getFinishRedirectUrl, finishRedirectUrl)
                .set(StringUtils.isNotBlank(ipWhiteList), MerchantConfig::getIpWhiteList, ipWhiteList)
                .eq(MerchantConfig::getId, merchantConfig.getId());
        return merchantConfigService.update(updateWrapper);
    }


    @Override
    public boolean updatePaymentLinkSetting(PaymentLinkSettingCmd command) {
        log.info("updatePaymentLinkSetting command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();
        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, merchantId);
        MerchantConfig merchantConfig = merchantConfigService.getOne(configQuery);
        Assert.notNull(merchantConfig, () -> new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST,
                merchantId));

        //操作数据库
        UpdateWrapper<MerchantConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(MerchantConfig::getPaymentLinkSetting, command.getPaymentLinkSetting())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(MerchantConfig::getId, merchantConfig.getId());
        return merchantConfigService.update(updateWrapper);
    }


}
