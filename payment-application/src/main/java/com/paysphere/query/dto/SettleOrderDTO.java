package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SettleOrderDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 结算单号
     */
    private String settleNo;

    /**
     * 结算类型 D0/D1
     */
    private String settleType;

    /**
     * 结算时间(商户配置，非D0使用)
     */
    private String settleTime;

    /**
     * 订单单号
     */
    private String tradeNo;

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 支付完成时间
     */
    private LocalDateTime paymentFinishTime;

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
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 扣费方式
     */
    private Integer deductionType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 结算商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 结算商户分润
     */
    private BigDecimal merchantProfit;

    /**
     * 结算商户通道成本
     */
    private BigDecimal channelCost;

    /**
     * 平台盈利
     */
    private BigDecimal platformProfit;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 结算时间
     */
    private LocalDateTime actualSettleTime;

    /**
     * 结算状态
     */
    private Integer settleStatus;

    /**
     * 关联外部单号
     */
    private String outerNo;


}
