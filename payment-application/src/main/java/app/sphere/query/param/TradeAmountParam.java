package app.sphere.query.param;

import lombok.Data;

@Data
public class TradeAmountParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
