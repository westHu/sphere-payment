package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SandboxTradePaymentOrderRepository;
import infrastructure.sphere.db.entity.SandboxTradePaymentOrder;
import infrastructure.sphere.db.mapper.SandboxTradePayOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class SandboxTradePaymentOrderRepositoryImpl extends ServiceImpl<SandboxTradePayOrderMapper, SandboxTradePaymentOrder>
        implements SandboxTradePaymentOrderRepository {
}
