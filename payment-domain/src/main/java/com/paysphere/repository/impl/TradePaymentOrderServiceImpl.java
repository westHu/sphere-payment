package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.db.mapper.TradePaymentOrderMapper;
import com.paysphere.repository.TradePaymentOrderService;
import org.springframework.stereotype.Service;

@Service
public class TradePaymentOrderServiceImpl extends ServiceImpl<TradePaymentOrderMapper, TradePaymentOrder>
        implements TradePaymentOrderService {
}
