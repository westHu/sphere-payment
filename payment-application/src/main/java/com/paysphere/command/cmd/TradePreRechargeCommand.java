package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradePreRechargeCommand {

    /**
     * 充值目的
     */
    private String purpose;

    /**
     * 充值商户ID
     */
    private String merchantId;

    /**
     * 充值商户名称
     */
    private String merchantName;

    /**
     * 充值账户
     */
    private String accountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     */
    private String paymentMethod;

}
