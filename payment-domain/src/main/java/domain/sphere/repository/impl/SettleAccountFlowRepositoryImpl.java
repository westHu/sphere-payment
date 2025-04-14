package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SettleAccountFlowRepository;
import infrastructure.sphere.db.entity.SettleAccountFlow;
import infrastructure.sphere.db.mapper.SettleAccountFlowMapper;
import org.springframework.stereotype.Service;

@Service
public class SettleAccountFlowRepositoryImpl extends ServiceImpl<SettleAccountFlowMapper, SettleAccountFlow>
        implements SettleAccountFlowRepository {
}
