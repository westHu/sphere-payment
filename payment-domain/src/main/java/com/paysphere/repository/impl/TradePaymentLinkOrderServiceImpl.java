package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradePaymentLinkOrder;
import com.paysphere.db.mapper.TradePaymentLinkOrderMapper;
import com.paysphere.repository.TradePaymentLinkOrderService;
import org.springframework.stereotype.Service;

@Service
public class TradePaymentLinkOrderServiceImpl extends ServiceImpl<TradePaymentLinkOrderMapper, TradePaymentLinkOrder>
        implements TradePaymentLinkOrderService {
}
