package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import infrastructure.sphere.db.entity.SettleOrder;
import infrastructure.sphere.db.mapper.SettleOrderMapper;
import domain.sphere.repository.SettleOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class SettleOrderRepositoryImpl extends ServiceImpl<SettleOrderMapper, SettleOrder>
        implements SettleOrderRepository {
}
