package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradeSnapshotTransferStatistics;
import com.paysphere.db.mapper.TradeSnapshotTransferStatisticsMapper;
import com.paysphere.repository.TradeSnapshotTransferStatisticsService;
import org.springframework.stereotype.Service;

@Service
public class TradeSnapshotTransferStatisticsServiceImpl extends ServiceImpl<TradeSnapshotTransferStatisticsMapper,
        TradeSnapshotTransferStatistics>
        implements TradeSnapshotTransferStatisticsService {
}
