package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantBaseConfig;
import com.paysphere.db.mapper.MerchantBaseConfigMapper;
import com.paysphere.repository.MerchantBaseConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantBaseConfigServiceImpl extends ServiceImpl<MerchantBaseConfigMapper, MerchantBaseConfig>
        implements MerchantBaseConfigService {
}
