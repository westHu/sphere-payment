package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.db.mapper.PaymentChannelMapper;
import com.paysphere.repository.PaymentChannelService;
import org.springframework.stereotype.Service;

@Service
public class PaymentChannelServiceImpl extends ServiceImpl<PaymentChannelMapper, PaymentChannel>
        implements PaymentChannelService {
}
