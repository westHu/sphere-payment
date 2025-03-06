package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentChannelMethodAddCommand {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 支付方向:1收款2出款
     */
    private Integer paymentDirection;

    /**
     * 支付方式属性
     */
    private String paymentAttribute;

    /**
     * 支付方式描述
     */
    private String description;

    /**
     * 单笔手续费
     */
    private BigDecimal singleFee;

    /**
     * 单笔手续费率
     */
    private BigDecimal singleRate;

    /**
     * 限额下限
     */
    private BigDecimal amountLimitMin;

    /**
     * 限额上限
     */
    private BigDecimal amountLimitMax;

    /**
     * 结算类型
     */
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;


}
