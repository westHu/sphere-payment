package com.paysphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.paysphere.db.entity.TradeSnapshotTradeMerchantStatistics;
import com.paysphere.db.mapper.TradeSnapshotTradeMerchantStatisticsMapper;
import com.paysphere.repository.TradeSnapshotTradeMerchantStatisticsService;
import org.springframework.stereotype.Service;

@Service
public class TradeSnapshotTradeMerchantStatisticsServiceImpl extends ServiceImpl<TradeSnapshotTradeMerchantStatisticsMapper,
        TradeSnapshotTradeMerchantStatistics>
        implements TradeSnapshotTradeMerchantStatisticsService {
}
