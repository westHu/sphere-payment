package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyCommand {

    /**
     * 币种
     */
    private String currency;

    /**
     * how mush to transaction
     * 金额
     */
    private BigDecimal amount;
}
