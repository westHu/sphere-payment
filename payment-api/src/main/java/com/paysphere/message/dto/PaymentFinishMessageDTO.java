package com.paysphere.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收款回调
 */
@Data
public class PaymentFinishMessageDTO {

    /**
     * 交易单号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 支付方向，1收款、2出款
     */
    private Integer paymentDirection;

    /**
     * 渠道编码
     */
    @NotBlank(message = "channelCode is required")
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 支付方式编码
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    @NotNull(message = "paymentStatus is required")
    private Integer paymentStatus;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 完成时间
     */
    @NotBlank(message = "transactionTime is required")
    private String transactionTime;


    /**
     * Fees charged for each transaction
     * 单笔费用
     */
   private BigDecimal singleFee;

    /**
     * Fees charged for each transaction rate
     * 单笔费率
     */
   private BigDecimal singleRate;

    /**
     * 通道成本
     */
    private BigDecimal channelCost;

    /**
     * qr二维码 null/静态/动态
     */
    private Integer qrCodeType;

    /**
     * 扩展信息
     */
    private String additionalInfo;
}
