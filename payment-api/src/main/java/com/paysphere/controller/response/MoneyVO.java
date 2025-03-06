package com.paysphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyVO {

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private BigDecimal amount;

}
