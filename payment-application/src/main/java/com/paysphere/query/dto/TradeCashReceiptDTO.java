package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeCashReceiptDTO {

    /**
     * 代付单号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 支付完成时间
     */
    private Integer paymentFinishTime;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 出款账号
     */
    private String cashAccount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 代付金额
     */
    private BigDecimal amount;

    /**
     * 交易目的
     */
    private String purpose;

}
