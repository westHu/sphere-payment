package app.sphere.command.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import share.sphere.TradeConstant;
import app.sphere.command.SandboxMerchantConfigCmdService;
import app.sphere.command.cmd.SandboxMerchantConfigUpdateCommand;
import infrastructure.sphere.db.entity.SandboxMerchantConfig;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.SandboxMerchantConfigRepository;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static share.sphere.exception.ExceptionCode.MERCHANT_CONFIG_NOT_EXIST;

@Service
public class SandboxMerchantConfigCmdServiceImpl implements SandboxMerchantConfigCmdService {

    @Resource
    SandboxMerchantConfigRepository sandboxMerchantConfigRepository;

    @Override
    public Boolean updateSandboxMerchantConfig(SandboxMerchantConfigUpdateCommand command) {
        String merchantId = command.getMerchantId();
        QueryWrapper<SandboxMerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(SandboxMerchantConfig::getMerchantId, merchantId);
        SandboxMerchantConfig sandboxMerchantConfig = sandboxMerchantConfigRepository.getOne(configQuery);
        Assert.notNull(sandboxMerchantConfig, () -> new PaymentException(MERCHANT_CONFIG_NOT_EXIST, merchantId));

        UpdateWrapper<SandboxMerchantConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(StringUtils.isNotBlank(command.getFinishPaymentUrl()), SandboxMerchantConfig::getFinishPaymentUrl,
                        command.getFinishPaymentUrl())
                .set(StringUtils.isNotBlank(command.getFinishCashUrl()), SandboxMerchantConfig::getFinishPayoutUrl,
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
                .eq(SandboxMerchantConfig::getId, sandboxMerchantConfig.getId());
        return sandboxMerchantConfigRepository.update(updateWrapper);
    }
}
