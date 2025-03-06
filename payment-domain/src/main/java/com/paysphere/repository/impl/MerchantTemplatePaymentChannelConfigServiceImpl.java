package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantTemplatePaymentChannelConfig;
import com.paysphere.db.mapper.MerchantTemplatePayPaymentConfigMapper;
import com.paysphere.repository.MerchantTemplatePaymentChannelConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantTemplatePaymentChannelConfigServiceImpl
        extends ServiceImpl<MerchantTemplatePayPaymentConfigMapper, MerchantTemplatePaymentChannelConfig>
        implements MerchantTemplatePaymentChannelConfigService {
}
