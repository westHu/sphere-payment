package com.paysphere.query.param;

import lombok.Data;

@Data
public class SettleAgentAccountProfitParam {

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 起始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

}
