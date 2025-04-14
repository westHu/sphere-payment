package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TradeMerchantStatisticsDTO {

    /**
     * 当日收款总额
     */
    private BigDecimal payAmount = BigDecimal.ZERO;

    /**
     * 当日出款总额
     */
    private BigDecimal cashAmount = BigDecimal.ZERO;

    /**
     * 交易快照
     */
    private List<TradeMerchantStatisticsSnapshotDTO> snapshotTradeStatisticsList;

}
