package app.sphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import share.sphere.TradeConstant;
import app.sphere.command.MerchantConfigCmdService;
import app.sphere.command.cmd.MerchantConfigUpdateCmd;
import app.sphere.command.cmd.PaymentLinkSettingCmd;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantConfig;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.MerchantConfigRepository;
import domain.sphere.repository.MerchantRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MerchantConfigCmdServiceImpl implements MerchantConfigCmdService {

    @Resource
    MerchantRepository merchantRepository;
    @Resource
    MerchantConfigRepository merchantConfigRepository;

    /**
     * 更新商户配置
     */
    @Override
    public boolean updateMerchantConfig(MerchantConfigUpdateCmd cmd) {
        log.info("updateMerchantConfig cmd={}", JSONUtil.toJsonStr(cmd));

        String merchantId = cmd.getMerchantId();
        String finishPaymentUrl = cmd.getFinishPaymentUrl();
        String finishPayoutUrl = cmd.getFinishPayoutUrl();
        String finishRefundUrl = cmd.getFinishRefundUrl();
        String finishRedirectUrl = cmd.getFinishRedirectUrl();
        String ipWhiteList = cmd.getIpWhiteList();

        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, merchantId);
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, merchantId));

        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, merchantId);
        MerchantConfig merchantConfig = merchantConfigRepository.getOne(configQuery);
        Assert.notNull(merchantConfig, () -> new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, merchantId));

        UpdateWrapper<MerchantConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(StringUtils.isNotBlank(finishPaymentUrl), MerchantConfig::getFinishPaymentUrl, finishPaymentUrl)
                .set(StringUtils.isNotBlank(finishPayoutUrl), MerchantConfig::getFinishPayoutUrl, finishPayoutUrl)
                .set(StringUtils.isNotBlank(finishRefundUrl), MerchantConfig::getFinishRefundUrl, finishRefundUrl)
                .set(StringUtils.isNotBlank(finishRedirectUrl), MerchantConfig::getFinishRedirectUrl, finishRedirectUrl)
                .set(StringUtils.isNotBlank(ipWhiteList), MerchantConfig::getIpWhiteList, ipWhiteList)
                .eq(MerchantConfig::getId, merchantConfig.getId());
        return merchantConfigRepository.update(updateWrapper);
    }

    /**
     * 更新支付链接样式
     */
    @Override
    public boolean updatePaymentLinkSetting(PaymentLinkSettingCmd command) {
        log.info("updatePaymentLinkSetting command={}", JSONUtil.toJsonStr(command));
        String merchantId = command.getMerchantId();

        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, merchantId);
        MerchantConfig merchantConfig = merchantConfigRepository.getOne(configQuery);
        Assert.notNull(merchantConfig, () -> new PaymentException(ExceptionCode.MERCHANT_CONFIG_NOT_EXIST, merchantId));

        //操作数据库
        UpdateWrapper<MerchantConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(MerchantConfig::getPaymentLinkSetting, command.getPaymentLinkSetting())
                .eq(MerchantConfig::getId, merchantConfig.getId());
        return merchantConfigRepository.update(updateWrapper);
    }


}
