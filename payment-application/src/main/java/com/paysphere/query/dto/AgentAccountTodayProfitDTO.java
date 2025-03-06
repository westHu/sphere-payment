package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentAccountTodayProfitDTO {

    /**
     * 当日收款佣金
     */
    private BigDecimal payinAgentProfit = BigDecimal.ZERO;

    /**
     * 当日收款佣金
     */
    private BigDecimal payoutAgentProfit = BigDecimal.ZERO;

}
