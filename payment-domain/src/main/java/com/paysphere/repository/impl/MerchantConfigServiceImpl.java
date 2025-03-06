package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.mapper.MerchantConfigMapper;
import com.paysphere.repository.MerchantConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantConfigServiceImpl extends ServiceImpl<MerchantConfigMapper, MerchantConfig>
        implements MerchantConfigService {
}
