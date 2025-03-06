package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradeSnapshotTradeStatistics;
import com.paysphere.db.mapper.TradeSnapshotTradeStatisticsMapper;
import com.paysphere.repository.TradeSnapshotTradeStatisticsService;
import org.springframework.stereotype.Service;

@Service
public class TradeSnapshotTradeStatisticsServiceImpl extends ServiceImpl<TradeSnapshotTradeStatisticsMapper,
        TradeSnapshotTradeStatistics>
        implements TradeSnapshotTradeStatisticsService {
}
