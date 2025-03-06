package com.paysphere.query.param;

import lombok.Data;

@Data
public class TradeStatisticsByAgentPageParam extends PageParam {

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
