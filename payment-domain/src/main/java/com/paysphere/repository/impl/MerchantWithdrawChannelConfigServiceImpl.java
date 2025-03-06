package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantWithdrawChannelConfig;
import com.paysphere.db.mapper.MerchantWithdrawPaymentConfigMapper;
import com.paysphere.repository.MerchantWithdrawChannelConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantWithdrawChannelConfigServiceImpl extends ServiceImpl<MerchantWithdrawPaymentConfigMapper,
        MerchantWithdrawChannelConfig>
        implements MerchantWithdrawChannelConfigService {
}
