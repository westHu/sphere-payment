package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradePaymentLinkCmd {

    /**
     * 地区
     */
    private Integer area;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 过期时间（秒）
     */
    private Integer expiryPeriod;

    /**
     * 通知邮箱
     */
    private String notificationEmail;

    /**
     * 备注
     */
    private String notes;

}
