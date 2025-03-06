package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class TradeTimelyStatisticsIndexDTO {

    /**
     * 收款
     */
    private Integer payCount = 0;
    private Integer paySuccessCount = 0;
    private BigDecimal payAmount = BigDecimal.ZERO;
    private BigDecimal paySuccessAmount = BigDecimal.ZERO;
    private BigDecimal paySuccessRate = BigDecimal.ZERO;


    /**
     * 收款
     */
    private Integer cashCount = 0;
    private Integer cashSuccessCount = 0;
    private BigDecimal cashAmount = BigDecimal.ZERO;
    private BigDecimal cashSuccessAmount = BigDecimal.ZERO;
    private BigDecimal cashSuccessRate = BigDecimal.ZERO;

    /**
     * 以往数据 近7天
     */
    private List<TradeTimelyStatisticsIndexSnapshotDTO> snapshotTradeStatisticsList = new ArrayList<>();

}
