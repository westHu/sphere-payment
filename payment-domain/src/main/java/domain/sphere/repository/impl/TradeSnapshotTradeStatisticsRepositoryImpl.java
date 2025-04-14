package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.TradeSnapshotTradeStatisticsRepository;
import infrastructure.sphere.db.entity.TradeSnapshotTradeStatistics;
import infrastructure.sphere.db.mapper.TradeSnapshotTradeStatisticsMapper;
import org.springframework.stereotype.Service;

@Service
public class TradeSnapshotTradeStatisticsRepositoryImpl extends ServiceImpl<TradeSnapshotTradeStatisticsMapper,
        TradeSnapshotTradeStatistics>
        implements TradeSnapshotTradeStatisticsRepository {
}
