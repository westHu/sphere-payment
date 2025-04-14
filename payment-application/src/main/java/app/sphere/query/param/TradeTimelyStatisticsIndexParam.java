package app.sphere.query.param;

import lombok.Data;

@Data
public class TradeTimelyStatisticsIndexParam {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
