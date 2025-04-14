package app.sphere.query.param;

import lombok.Data;

@Data
public class SettleAccountSnapshotParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;
}
