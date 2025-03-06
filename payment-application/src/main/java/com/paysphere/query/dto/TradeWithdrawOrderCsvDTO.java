package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeWithdrawOrderCsvDTO {

    /**
     * 提现单号
     */
    private String tradeNo;

    /**
     * 提现目的
     */
    private String purpose;

    /**
     * 提现商户ID
     */
    private String merchantId;

    /**
     * 提现商户名称
     */
    private String merchantName;

    /**
     * 提现账户
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

    /**
     * 提现账户
     */
    private String withdrawAccount;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 交易状态
     */
    private String tradeStatus;


}
