package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentChannelMethodAddReq {

    /**
     * 渠道编码
     */
    @NotBlank(message = "channelCode is blank")
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 支付方式编码
     */
    @NotBlank(message = "paymentMethod is blank")
    private String paymentMethod;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 支付方向:1收款2出款
     */
    @NotNull(message = "paymentDirection is null")
    private Integer paymentDirection;

    /**
     * 支付方式属性
     */
    @NotBlank(message = "paymentAttribute is blank")
    private String paymentAttribute;

    /**
     * 支付方式描述
     */
    private String description;

    /**
     * 单笔手续费
     */
    @NotNull(message = "singleFee is null")
    private BigDecimal singleFee;

    /**
     * 单笔手续费率
     */
    @NotNull(message = "singleRate is null")
    private BigDecimal singleRate;

    /**
     * 限额下限
     */
    @NotNull(message = "amountLimitMin is blank")
    private BigDecimal amountLimitMin;

    /**
     * 限额上限
     */
    @NotNull(message = "amountLimitMax is blank")
    private BigDecimal amountLimitMax;

    /**
     * 结算类型
     */
    @NotBlank(message = "settleType is null")
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;

}
