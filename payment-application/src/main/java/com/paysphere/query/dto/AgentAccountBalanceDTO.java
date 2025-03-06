package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentAccountBalanceDTO {

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 币种
     */
    private String currency;

    /**
     * 可用金额
     */
    private BigDecimal availableBalance = BigDecimal.ZERO;

    /**
     * 冻结金额
     */
    private BigDecimal frozenBalance = BigDecimal.ZERO;

    /**
     * 待结算金额
     */
    private BigDecimal toSettleBalance = BigDecimal.ZERO;


}
