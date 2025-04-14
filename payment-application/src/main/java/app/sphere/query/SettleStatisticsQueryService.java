package app.sphere.query;

import app.sphere.query.dto.SettleTimelyStatisticsIndexDTO;
import app.sphere.query.param.SettleTimelyStatisticsIndexParam;

public interface SettleStatisticsQueryService {

    SettleTimelyStatisticsIndexDTO getSettleTimelyStatistics4Index(SettleTimelyStatisticsIndexParam param);
}
