package com.paysphere.query.param;

import lombok.Data;

@Data
public class TradeMerchantStatisticsSnapshotParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 是否包含当天
     */
    private boolean includeToday;

}
