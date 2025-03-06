package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantPaymentChannelConfig;
import com.paysphere.db.mapper.MerchantPayPaymentChannelConfigMapper;
import com.paysphere.repository.MerchantPaymentChannelConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantPaymentChannelConfigServiceImpl
        extends ServiceImpl<MerchantPayPaymentChannelConfigMapper, MerchantPaymentChannelConfig>
        implements MerchantPaymentChannelConfigService {
}
