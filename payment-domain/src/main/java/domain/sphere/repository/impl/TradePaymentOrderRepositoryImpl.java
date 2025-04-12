package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.db.mapper.TradePaymentOrderMapper;
import domain.sphere.repository.TradePaymentOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class TradePaymentOrderRepositoryImpl extends ServiceImpl<TradePaymentOrderMapper, TradePaymentOrder>
        implements TradePaymentOrderRepository {
}
