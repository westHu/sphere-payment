package app.sphere.query;

import app.sphere.query.param.SettleTimelyStatisticsIndexParam;
import app.sphere.query.dto.SettleTimelyStatisticsIndexDTO;

public interface SettleStatisticsQueryService {

    SettleTimelyStatisticsIndexDTO getSettleTimelyStatistics4Index(SettleTimelyStatisticsIndexParam param);
}
