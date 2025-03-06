package com.paysphere.command.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantProfitDTO {

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 代理商名称
     */
    private String agentName;

    /**
     * 代理商账户号
     */
    private String accountNo;

    /**
     * 代理商账户号
     */
    private String accountName;

    /**
     * 代理商分润
     */
    private BigDecimal merchantProfit;

}
