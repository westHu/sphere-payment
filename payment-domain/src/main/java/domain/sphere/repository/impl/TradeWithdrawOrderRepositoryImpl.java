package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradeWithdrawOrderRepository;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import infrastructure.sphere.db.mapper.TradeWithdrawOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class TradeWithdrawOrderRepositoryImpl extends ServiceImpl<TradeWithdrawOrderMapper, TradeWithdrawOrder>
        implements TradeWithdrawOrderRepository {
}
