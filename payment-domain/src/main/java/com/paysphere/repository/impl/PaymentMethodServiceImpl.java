package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.PaymentMethod;
import com.paysphere.db.mapper.PaymentMethodMapper;
import com.paysphere.repository.PaymentMethodService;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodServiceImpl extends ServiceImpl<PaymentMethodMapper, PaymentMethod>
        implements PaymentMethodService {
}
