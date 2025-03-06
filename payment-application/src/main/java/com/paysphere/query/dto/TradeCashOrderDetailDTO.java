package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeCashOrderDetailDTO {

    /**
     * 收款单号
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
     * 扣费方式
     */
    private Integer deductionType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 收款金额
     */
    private BigDecimal amount;

    /**
     * 实扣金额
     */
    private BigDecimal actualAmount;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 通道成本
     */
    private BigDecimal channelCost;

    /**
     * 商户入账金额
     */
    private BigDecimal accountAmount;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 回调状态
     */
    private Integer callBackStatus;

    /**
     * 结算状态
     */
    private Integer settleStatus;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 收款人姓名
     */
    private String cashName;

    /**
     * 收款人电话
     */
    private String cashPhone;

    /**
     * 收款人邮箱
     */
    private String cashEmail;

    /**
     * 收款人账户号
     */
    private String cashAccount;

    /**
     * 收款人银行
     */
    private String cashBank;

    /**
     * 收款人银行代码
     */
    private String cashBankCode;

    /**
     * 交易目的（备注）
     */
    private String purpose;

    /**
     * 交易时间（开始时间）
     */
    private Integer tradeTime;

    /**
     * 支付完成时间（结束时间）
     */
    private Integer paymentFinishTime;


}
