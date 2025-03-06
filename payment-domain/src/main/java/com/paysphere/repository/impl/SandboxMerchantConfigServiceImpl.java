package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.SandboxMerchantConfig;
import com.paysphere.db.mapper.SandboxMerchantConfigMapper;
import com.paysphere.repository.SandboxMerchantConfigService;
import org.springframework.stereotype.Service;

@Service
public class SandboxMerchantConfigServiceImpl extends ServiceImpl<SandboxMerchantConfigMapper, SandboxMerchantConfig>
        implements SandboxMerchantConfigService {
}
