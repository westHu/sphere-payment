package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantPaymentConfig;
import com.paysphere.db.mapper.MerchantPayConfigMapper;
import com.paysphere.repository.MerchantPaymentConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantPaymentConfigServiceImpl extends ServiceImpl<MerchantPayConfigMapper, MerchantPaymentConfig>
        implements MerchantPaymentConfigService {
}
