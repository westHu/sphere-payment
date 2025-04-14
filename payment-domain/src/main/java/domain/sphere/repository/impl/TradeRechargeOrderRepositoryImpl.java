package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradeRechargeOrderRepository;
import infrastructure.sphere.db.entity.TradeRechargeOrder;
import infrastructure.sphere.db.mapper.TradeRechargeOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class TradeRechargeOrderRepositoryImpl extends ServiceImpl<TradeRechargeOrderMapper, TradeRechargeOrder>
        implements TradeRechargeOrderRepository {
}
