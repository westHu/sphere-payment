package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.SandboxTradePaymentOrder;
import com.paysphere.db.mapper.SandboxTradePayOrderMapper;
import com.paysphere.repository.SandboxTradePayOrderService;
import org.springframework.stereotype.Service;

@Service
public class SandboxTradePayOrderServiceImpl extends ServiceImpl<SandboxTradePayOrderMapper, SandboxTradePaymentOrder>
        implements SandboxTradePayOrderService {
}
