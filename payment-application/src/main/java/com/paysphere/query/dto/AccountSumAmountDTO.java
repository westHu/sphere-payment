package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountSumAmountDTO {

    /**
     * 商户总数
     */
    private Integer merchantCount;

    /**
     * 商户总金额
     */
    private BigDecimal merchantSumAmount;

    /**
     * 平台总金额
     */
    private BigDecimal platformSumAmount;

}
