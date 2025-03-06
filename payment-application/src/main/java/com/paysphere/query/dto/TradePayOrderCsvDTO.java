package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradePayOrderCsvDTO {

    /**
     * 收款单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String outerNo;

    /**
     * 交易目的
     */
    private String purpose;

    /**
     * 交易item明细
     */
    private String itemDetailInfo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道信息
     */
    private String channelCode;

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
     * 收款金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal merchantFee;

    /**
     * 付款方信息
     */
    // private String payerInfo;

    /**
     * 收款方信息
     */
    // private String receiverInfo;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 支付完成时间
     */
    private String paymentFinishTime;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 支付状态
     */
    private String paymentStatus;

    /**
     * 交易结果-Va
     */
    private String tradeResultVa;

}
