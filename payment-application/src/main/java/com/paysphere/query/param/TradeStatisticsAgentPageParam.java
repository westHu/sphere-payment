package com.paysphere.query.param;

import lombok.Data;

@Data
public class TradeStatisticsAgentPageParam extends PageParam {

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
