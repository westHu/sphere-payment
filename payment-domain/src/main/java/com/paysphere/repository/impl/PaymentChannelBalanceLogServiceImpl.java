package com.paysphere.repository.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.PaymentChannelBalanceLog;
import com.paysphere.db.mapper.PaymentChannelBalanceLogMapper;
import com.paysphere.repository.PaymentChannelBalanceLogService;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class PaymentChannelBalanceLogServiceImpl extends ServiceImpl<PaymentChannelBalanceLogMapper,
        PaymentChannelBalanceLog> implements PaymentChannelBalanceLogService {

}
