package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantPayoutChannelConfig;
import com.paysphere.db.mapper.MerchantCashPaymentChannelConfigMapper;
import com.paysphere.repository.MerchantPayoutChannelConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantPayoutChannelConfigServiceImpl
        extends ServiceImpl<MerchantCashPaymentChannelConfigMapper, MerchantPayoutChannelConfig>
        implements MerchantPayoutChannelConfigService {
}
