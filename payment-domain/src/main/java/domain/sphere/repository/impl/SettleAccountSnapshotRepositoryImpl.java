package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SettleAccountSnapshotRepository;
import infrastructure.sphere.db.entity.SettleAccountSnapshot;
import infrastructure.sphere.db.mapper.SettleAccountSnapshotMapper;
import org.springframework.stereotype.Service;

@Service
public class SettleAccountSnapshotRepositoryImpl extends ServiceImpl<SettleAccountSnapshotMapper, SettleAccountSnapshot>
        implements SettleAccountSnapshotRepository {
}
