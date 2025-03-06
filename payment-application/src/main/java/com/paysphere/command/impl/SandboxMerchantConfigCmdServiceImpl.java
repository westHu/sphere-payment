package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.SandboxMerchantConfigCmdService;
import com.paysphere.command.cmd.SandboxMerchantConfigUpdateCommand;
import com.paysphere.db.entity.SandboxMerchantConfig;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.SandboxMerchantConfigService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.paysphere.exception.ExceptionCode.MERCHANT_CONFIG_NOT_EXIST;

@Service
public class SandboxMerchantConfigCmdServiceImpl implements SandboxMerchantConfigCmdService {

    @Resource
    SandboxMerchantConfigService sandboxMerchantConfigService;


    @Override
    public Boolean updateSandboxMerchantConfig(SandboxMerchantConfigUpdateCommand command) {
        String merchantId = command.getMerchantId();
        QueryWrapper<SandboxMerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(SandboxMerchantConfig::getMerchantId, merchantId);
        SandboxMerchantConfig sandboxMerchantConfig = sandboxMerchantConfigService.getOne(configQuery);
        Assert.notNull(sandboxMerchantConfig, () -> new PaymentException(MERCHANT_CONFIG_NOT_EXIST, merchantId));

        UpdateWrapper<SandboxMerchantConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(StringUtils.isNotBlank(command.getFinishPaymentUrl()), SandboxMerchantConfig::getFinishPaymentUrl,
                        command.getFinishPaymentUrl())
                .set(StringUtils.isNotBlank(command.getFinishCashUrl()), SandboxMerchantConfig::getFinishCashUrl,
                        command.getFinishCashUrl())
                .set(StringUtils.isNotBlank(command.getFinishRefundUrl()), SandboxMerchantConfig::getFinishRefundUrl,
                        command.getFinishRefundUrl())
                .set(StringUtils.isNotBlank(command.getFinishRedirectUrl()),
                        SandboxMerchantConfig::getFinishRedirectUrl,
                        command.getFinishRedirectUrl())
                .set(StringUtils.isNotBlank(command.getPublicKey()), SandboxMerchantConfig::getPublicKey,
                        command.getPublicKey())
                .set(StringUtils.isNotBlank(command.getIpWhiteList()), SandboxMerchantConfig::getIpWhiteList,
                        command.getIpWhiteList())
                .setSql(TradeConstant.VERSION_SQL)
                .eq(SandboxMerchantConfig::getId, sandboxMerchantConfig.getId())
                .eq(SandboxMerchantConfig::getVersion, sandboxMerchantConfig.getVersion());
        return sandboxMerchantConfigService.update(updateWrapper);
    }
}
