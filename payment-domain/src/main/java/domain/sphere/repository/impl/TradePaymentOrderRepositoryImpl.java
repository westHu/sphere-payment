package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradePaymentOrderRepository;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.db.mapper.TradePaymentOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class TradePaymentOrderRepositoryImpl extends ServiceImpl<TradePaymentOrderMapper, TradePaymentOrder>
        implements TradePaymentOrderRepository {
}
