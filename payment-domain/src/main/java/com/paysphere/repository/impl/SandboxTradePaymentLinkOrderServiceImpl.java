package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.SandboxTradePaymentLinkOrder;
import com.paysphere.db.mapper.SandboxTradePaymentLinkOrderMapper;
import com.paysphere.repository.SandboxTradePaymentLinkOrderService;
import org.springframework.stereotype.Service;

@Service
public class SandboxTradePaymentLinkOrderServiceImpl
        extends ServiceImpl<SandboxTradePaymentLinkOrderMapper, SandboxTradePaymentLinkOrder>
        implements SandboxTradePaymentLinkOrderService {
}
