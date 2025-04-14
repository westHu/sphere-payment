package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SandboxTradePaymentLinkOrderRepository;
import infrastructure.sphere.db.entity.SandboxTradePaymentLinkOrder;
import infrastructure.sphere.db.mapper.SandboxTradePaymentLinkOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class SandboxTradePaymentLinkOrderRepositoryImpl
        extends ServiceImpl<SandboxTradePaymentLinkOrderMapper, SandboxTradePaymentLinkOrder>
        implements SandboxTradePaymentLinkOrderRepository {
}
