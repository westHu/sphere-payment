package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import infrastructure.sphere.db.mapper.TradePayoutOrderMapper;
import domain.sphere.repository.TradePayoutOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class TradePayoutOrderRepositoryImpl extends ServiceImpl<TradePayoutOrderMapper, TradePayoutOrder>
        implements TradePayoutOrderRepository {
}
