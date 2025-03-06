package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.db.mapper.MerchantOperatorMapper;
import com.paysphere.repository.MerchantOperatorService;
import org.springframework.stereotype.Service;

@Service
public class MerchantOperatorServiceImpl extends ServiceImpl<MerchantOperatorMapper, MerchantOperator>
        implements MerchantOperatorService {
}
