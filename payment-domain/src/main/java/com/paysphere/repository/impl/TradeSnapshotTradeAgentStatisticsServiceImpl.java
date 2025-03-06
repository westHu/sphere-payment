package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradeSnapshotTradeAgentStatistics;
import com.paysphere.db.mapper.TradeSnapshotTradeAgentStatisticsMapper;
import com.paysphere.repository.TradeSnapshotTradeAgentStatisticsService;
import org.springframework.stereotype.Service;

@Service
public class TradeSnapshotTradeAgentStatisticsServiceImpl extends ServiceImpl<TradeSnapshotTradeAgentStatisticsMapper,
        TradeSnapshotTradeAgentStatistics>
        implements TradeSnapshotTradeAgentStatisticsService {
}
