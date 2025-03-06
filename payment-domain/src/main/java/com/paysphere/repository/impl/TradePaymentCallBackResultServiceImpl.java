package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradePaymentCallBackResult;
import com.paysphere.db.mapper.TradePaymentCallBackResultMapper;
import com.paysphere.repository.TradePaymentCallBackResultService;
import org.springframework.stereotype.Service;

@Service
public class TradePaymentCallBackResultServiceImpl extends ServiceImpl<TradePaymentCallBackResultMapper, TradePaymentCallBackResult>
        implements TradePaymentCallBackResultService {
}
