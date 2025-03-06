package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeTransferOrderCsvDTO {

    /**
     * 转账单号
     */
    private String tradeNo;

    /**
     * 转账目的
     */
    private String purpose;

    /**
     * 转账商户ID
     */
    private String merchantId;

    /**
     * 转账商户名称
     */
    private String merchantName;

    /**
     * 转账账户
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
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 交易时间
     */
    private String tradeTime;


}
