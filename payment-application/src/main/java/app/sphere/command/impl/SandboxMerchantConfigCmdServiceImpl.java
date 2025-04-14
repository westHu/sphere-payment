package app.sphere.command.impl;

import app.sphere.command.SandboxMerchantConfigCmdService;
import app.sphere.command.cmd.SandboxMerchantConfigUpdateCommand;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.MerchantSandboxConfigRepository;
import infrastructure.sphere.db.entity.MerchantSandboxConfig;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.exception.PaymentException;

import static share.sphere.exception.ExceptionCode.MERCHANT_CONFIG_NOT_EXIST;

@Service
public class SandboxMerchantConfigCmdServiceImpl implements SandboxMerchantConfigCmdService {

    @Resource
    MerchantSandboxConfigRepository merchantSandboxConfigRepository;

    @Override
    public Boolean updateSandboxMerchantConfig(SandboxMerchantConfigUpdateCommand command) {
        String merchantId = command.getMerchantId();
        QueryWrapper<MerchantSandboxConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantSandboxConfig::getMerchantId, merchantId);
        MerchantSandboxConfig merchantSandboxConfig = merchantSandboxConfigRepository.getOne(configQuery);
        Assert.notNull(merchantSandboxConfig, () -> new PaymentException(MERCHANT_CONFIG_NOT_EXIST, merchantId));

        UpdateWrapper<MerchantSandboxConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(StringUtils.isNotBlank(command.getFinishPaymentUrl()), MerchantSandboxConfig::getFinishPaymentUrl,
                        command.getFinishPaymentUrl())
                .set(StringUtils.isNotBlank(command.getFinishCashUrl()), MerchantSandboxConfig::getFinishPayoutUrl,
                        command.getFinishCashUrl())
                .set(StringUtils.isNotBlank(command.getFinishRefundUrl()), MerchantSandboxConfig::getFinishRefundUrl,
                        command.getFinishRefundUrl())
                .set(StringUtils.isNotBlank(command.getFinishRedirectUrl()),
                        MerchantSandboxConfig::getFinishRedirectUrl,
                        command.getFinishRedirectUrl())
                .set(StringUtils.isNotBlank(command.getPublicKey()), MerchantSandboxConfig::getPublicKey,
                        command.getPublicKey())
                .set(StringUtils.isNotBlank(command.getIpWhiteList()), MerchantSandboxConfig::getIpWhiteList,
                        command.getIpWhiteList())
                .eq(MerchantSandboxConfig::getId, merchantSandboxConfig.getId());
        return merchantSandboxConfigRepository.update(updateWrapper);
    }
}
