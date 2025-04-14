package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradePayoutOrderRepository;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import infrastructure.sphere.db.mapper.TradePayoutOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class TradePayoutOrderRepositoryImpl extends ServiceImpl<TradePayoutOrderMapper, TradePayoutOrder>
        implements TradePayoutOrderRepository {
}
