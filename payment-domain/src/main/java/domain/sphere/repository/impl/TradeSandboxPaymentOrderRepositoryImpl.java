package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradeSandboxPaymentOrderRepository;
import infrastructure.sphere.db.entity.TradeSandboxPaymentOrder;
import infrastructure.sphere.db.mapper.TradeSandboxPayOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class TradeSandboxPaymentOrderRepositoryImpl extends ServiceImpl<TradeSandboxPayOrderMapper, TradeSandboxPaymentOrder>
        implements TradeSandboxPaymentOrderRepository {
}
