package com.paysphere.controller.request;

import lombok.Data;


@Data
public class TradeStatisticsAgentPageReq extends PageReq {

    /**
     * 代理商父ID
     */
    private String agentParentId;

    /**
     * 开始时间
     */
    private String startTradeDate;

    /**
     * 结束时间
     */
    private String endTradeDate;
}
