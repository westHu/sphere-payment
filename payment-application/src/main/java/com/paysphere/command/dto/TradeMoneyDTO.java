package com.paysphere.command.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 金额
 */
@Data
public class TradeMoneyDTO {

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 交易手续费
     */
    private BigDecimal fee;

}
