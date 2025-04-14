package app.sphere.query.param;

import lombok.Data;

@Data
public class SettleAccountSnapshotListByDayParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 时间
     */
    private String accountDate;

}
