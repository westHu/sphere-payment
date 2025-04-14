package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.TradeTransferOrder;
import infrastructure.sphere.db.mapper.TradeTransferOrderMapper;
import domain.sphere.repository.TradeTransferOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class TradeTransferOrderRepositoryImpl extends ServiceImpl<TradeTransferOrderMapper, TradeTransferOrder>
        implements TradeTransferOrderRepository {
}
