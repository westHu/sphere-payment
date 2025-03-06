package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

/**
 * what is transaction
 * do not explanation
 */
@Data
public class ItemDetailCommand {

    /**
     * 支付标的名称
     */
    private String name;

    /**
     * 支付标的数量
     */
    private Integer quantity;

    /**
     * 支付标的价格
     */
    private BigDecimal price;
}
