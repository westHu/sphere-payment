package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.db.mapper.PaymentChannelMethodMapper;
import com.paysphere.repository.PaymentChannelMethodService;
import org.springframework.stereotype.Service;

@Service
public class PaymentChannelMethodServiceImpl extends ServiceImpl<PaymentChannelMethodMapper, PaymentChannelMethod>
        implements PaymentChannelMethodService {
}
