package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradeTransferOrder;
import com.paysphere.db.mapper.TradeTransferOrderMapper;
import com.paysphere.repository.TradeTransferOrderService;
import org.springframework.stereotype.Service;

@Service
public class TradeTransferOrderServiceImpl extends ServiceImpl<TradeTransferOrderMapper, TradeTransferOrder>
        implements TradeTransferOrderService {
}
