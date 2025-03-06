package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeTimelyStatisticsIndexSnapshotDTO {

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 交易金额
     */
    private BigDecimal amount = BigDecimal.ZERO;


}
