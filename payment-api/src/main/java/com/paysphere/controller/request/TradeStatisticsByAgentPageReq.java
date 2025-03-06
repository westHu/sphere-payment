package com.paysphere.controller.request;

import lombok.Data;

@Data
public class TradeStatisticsByAgentPageReq extends PageReq {

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 开始时间
     */
    private String startTradeDate;

    /**
     * 结束时间
     */
    private String endTradeDate;

}
