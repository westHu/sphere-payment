package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.SandboxTradePayoutOrder;
import infrastructure.sphere.db.mapper.SandboxTradePayoutOrderMapper;
import domain.sphere.repository.SandboxTradePayoutOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class SandboxTradePayoutOrderRepositoryImpl extends ServiceImpl<SandboxTradePayoutOrderMapper, SandboxTradePayoutOrder>
        implements SandboxTradePayoutOrderRepository {
}
