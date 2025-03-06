package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantTemplatePayoutChannelConfig;
import com.paysphere.db.mapper.MerchantTemplateCashPaymentConfigMapper;
import com.paysphere.repository.MerchantTemplatePayoutChannelConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantTemplatePayoutChannelConfigServiceImpl
        extends ServiceImpl<MerchantTemplateCashPaymentConfigMapper, MerchantTemplatePayoutChannelConfig>
        implements MerchantTemplatePayoutChannelConfigService {
}
