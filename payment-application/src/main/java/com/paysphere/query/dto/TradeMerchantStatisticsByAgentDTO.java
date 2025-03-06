package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TradeMerchantStatisticsByAgentDTO {

    /**
     * 交易日期 维度
     */
    private LocalDate tradeDate;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 收款订单笔数
     */
    private Integer payOrderCount;

    /**
     * 收款订单成功笔数
     */
    private Integer payOrderSuccessCount;

    /**
     * 订单金额
     */
    private BigDecimal payOrderAmount;

    /**
     * 收款订单成功金额
     */
    private BigDecimal payOrderSuccessAmount;

    /**
     * 收款订单成功率
     */
    private BigDecimal payOrderSuccessRate;

    /**
     * 代付订单笔数
     */
    private Integer cashOrderCount;

    /**
     * 代付订单成功笔数
     */
    private Integer cashOrderSuccessCount;

    /**
     * 代付订单金额
     */
    private BigDecimal cashOrderAmount;

    /**
     * 代付订单成功金额
     */
    private BigDecimal cashOrderSuccessAmount;

    /**
     * 代付订单成功率
     */
    private BigDecimal cashOrderSuccessRate;
}
