package com.paysphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeCashOrderPageVO {

    /**
     * 目的
     */
    private String purpose;

    /**
     * 代付单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String outerNo;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户账户号
     */
    private String accountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 代付金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal merchantFee;

    /**
     * 实扣金额
     */
    private BigDecimal actualAmount;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 回调状态
     */
    private Integer callBackStatus;

    /**
     * 交易时间
     */
    private Integer tradeTime;

    /**
     * 支付完成时间
     */
    private Integer paymentFinishTime;

    /**
     * 来源
     */
    private Integer source;
}
