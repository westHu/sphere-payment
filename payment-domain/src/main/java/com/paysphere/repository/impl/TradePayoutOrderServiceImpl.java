package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.db.mapper.TradePayoutOrderMapper;
import com.paysphere.repository.TradePayoutOrderService;
import org.springframework.stereotype.Service;

@Service
public class TradePayoutOrderServiceImpl extends ServiceImpl<TradePayoutOrderMapper, TradePayoutOrder>
        implements TradePayoutOrderService {
}
