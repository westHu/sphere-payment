package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeAmountDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 收款金额汇总
     */
    private BigDecimal payAmount = BigDecimal.ZERO;

    /**
     * 代付金额汇总
     */
    private BigDecimal cashAmount = BigDecimal.ZERO;

}
