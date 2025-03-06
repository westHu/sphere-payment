package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradeRechargeOrder;
import com.paysphere.db.mapper.TradeRechargeOrderMapper;
import com.paysphere.repository.TradeRechargeOrderService;
import org.springframework.stereotype.Service;

@Service
public class TradeRechargeOrderServiceImpl extends ServiceImpl<TradeRechargeOrderMapper, TradeRechargeOrder>
        implements TradeRechargeOrderService {
}
