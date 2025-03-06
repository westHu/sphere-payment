package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TradeDetailDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 收款金额
     */
    private BigDecimal payAmount = BigDecimal.ZERO;

    /**
     * 收款成功率
     */
    private BigDecimal payRate = BigDecimal.ZERO;

    /**
     * 代付金额
     */
    private BigDecimal cashAmount = BigDecimal.ZERO;

    /**
     * 代付成功率
     */
    private BigDecimal cashRate = BigDecimal.ZERO;

}
