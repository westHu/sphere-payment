package com.paysphere.command.dto.trade.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResultDTO {

    /**
     * 对接的渠道编码
     */
    private String channelCode;

    /**
     * 对接的渠道编码
     */
    private String channelName;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 交易方向 见枚举
     */
    private Integer paymentDirection;

    /**
     * 单笔费用
     */
    private BigDecimal singleFee;

    /**
     * 单笔费率
     */
    private BigDecimal singleRate;

    /**
     * 通道成本
     */
    private BigDecimal channelCost;

    /**
     * 平台利润
     */
    private BigDecimal platformProfit;
}
