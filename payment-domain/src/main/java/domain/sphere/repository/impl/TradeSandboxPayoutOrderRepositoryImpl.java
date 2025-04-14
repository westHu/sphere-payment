package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradeSandboxPayoutOrderRepository;
import infrastructure.sphere.db.entity.TradeSandboxPayoutOrder;
import infrastructure.sphere.db.mapper.TradeSandboxPayoutOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class TradeSandboxPayoutOrderRepositoryImpl extends ServiceImpl<TradeSandboxPayoutOrderMapper, TradeSandboxPayoutOrder>
        implements TradeSandboxPayoutOrderRepository {
}
