package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.PaymentCallBackMessage;
import com.paysphere.db.mapper.PaymentCallBackMessageMapper;
import com.paysphere.repository.PaymentCallBackMessageService;
import org.springframework.stereotype.Service;

@Service
public class PaymentCallBackMessageServiceImpl extends ServiceImpl<PaymentCallBackMessageMapper, PaymentCallBackMessage>
        implements PaymentCallBackMessageService {
}
