package app.sphere.query.param;

import lombok.Data;

@Data
public class TradeStatisticsPlatformParam extends PageParam {

    /**
     * 交易开始日期
     */
    private String tradeStartDate;

    /**
     * 交易结束日期
     */
    private String tradeEndDate;

    /**
     * 交易类型
     */
    private Integer tradeType;

}
