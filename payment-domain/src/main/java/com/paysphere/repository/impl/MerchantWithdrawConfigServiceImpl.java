package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantWithdrawConfig;
import com.paysphere.db.mapper.MerchantWithdrawConfigMapper;
import com.paysphere.repository.MerchantWithdrawConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantWithdrawConfigServiceImpl extends ServiceImpl<MerchantWithdrawConfigMapper, MerchantWithdrawConfig>
        implements MerchantWithdrawConfigService {
}
