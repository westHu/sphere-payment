package com.paysphere.mq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlePayMessageDTO {

    /**
     * 业务单号-
     */
    private String businessNo;

    /**
     * 交易单号
     */
    @NotBlank(message = "settlePay tradeNo is required")
    private String tradeNo;

    /**
     * 外部订单号
     */
    @NotBlank(message = "settlePay tradeNo is required")
    private String outerNo;

    /**
     * 交易时间
     */
    @NotBlank(message = "settlePay tradeTime is required")
    private String tradeTime;

    /**
     * 交易支付完成时间
     */
    @NotBlank(message = "settlePay paymentFinishTime is required")
    private String paymentFinishTime;

    /**
     * 币种
     */
    @NotBlank(message = "settlePay currency is required")
    private String currency;

    /**
     * 交易金额
     */
    @NotNull(message = "settlePay amount is required")
    private BigDecimal amount;

    /**
     * 商户手续费
     */
    @NotNull(message = "settlePay merchantFee is required")
    private BigDecimal merchantFee;

    /**
     * 代理商分润
     */
    @NotNull(message = "settlePay merchantProfit is required")
    private BigDecimal merchantProfit;

    /**
     * 代理商分润明细
     */
    private String merchantProfitList;

    /**
     * 通道成本
     */
    @NotNull(message = "settlePay channelCost is required")
    private BigDecimal channelCost;

    /**
     * 平台利润
     */
    @NotNull(message = "settlePay platformProfit is required")
    private BigDecimal platformProfit;

    /**
     * 到账金额
     */
    @NotNull(message = "settlePay accountAmount is required")
    private BigDecimal accountAmount;

    /**
     * 渠道信息
     */
    @NotBlank(message = "settlePay channelCode is required")
    private String channelCode;

    /**
     * 渠道名称
     */
    @NotBlank(message = "settlePay channelName is required")
    private String channelName;

    /**
     * 支付方式
     */
    @NotBlank(message = "settlePay paymentMethod is required")
    private String paymentMethod;

    /**
     * 支付方式
     */
    private String paymentName;

    /**
     * 商户信息
     */
    @NotBlank(message = "settlePay merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "settlePay merchantName is required")
    private String merchantName;

    /**
     * 商户账户号
     */
    @NotBlank(message = "settlePay accountNo is required")
    private String accountNo;

    /**
     * 扣费方式
     */
    private Integer deductionType;

    /**
     * 结算方式
     */
    @NotBlank(message = "settlePay settleType is required")
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;
}
