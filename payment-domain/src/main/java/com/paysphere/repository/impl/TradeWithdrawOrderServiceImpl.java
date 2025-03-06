package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradeWithdrawOrder;
import com.paysphere.db.mapper.TradeWithdrawOrderMapper;
import com.paysphere.repository.TradeWithdrawOrderService;
import org.springframework.stereotype.Service;

@Service
public class TradeWithdrawOrderServiceImpl extends ServiceImpl<TradeWithdrawOrderMapper, TradeWithdrawOrder>
        implements TradeWithdrawOrderService {
}
