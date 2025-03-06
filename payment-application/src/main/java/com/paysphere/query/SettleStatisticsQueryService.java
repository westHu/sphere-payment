package com.paysphere.query;

import com.paysphere.query.dto.SettleTimelyStatisticsIndexDTO;
import com.paysphere.query.param.SettleTimelyStatisticsIndexParam;

public interface SettleStatisticsQueryService {

    SettleTimelyStatisticsIndexDTO getSettleTimelyStatistics4Index(SettleTimelyStatisticsIndexParam param);
}
