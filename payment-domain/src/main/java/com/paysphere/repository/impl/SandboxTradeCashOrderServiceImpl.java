package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.SandboxTradePayoutOrder;
import com.paysphere.db.mapper.SandboxTradeCashOrderMapper;
import com.paysphere.repository.SandboxTradeCashOrderService;
import org.springframework.stereotype.Service;

@Service
public class SandboxTradeCashOrderServiceImpl extends ServiceImpl<SandboxTradeCashOrderMapper, SandboxTradePayoutOrder>
        implements SandboxTradeCashOrderService {
}
