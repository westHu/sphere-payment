package com.paysphere.query.param;

import lombok.Data;

@Data
public class SettleTimelyStatisticsIndexParam {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

}
