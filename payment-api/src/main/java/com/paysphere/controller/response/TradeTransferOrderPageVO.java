package com.paysphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeTransferOrderPageVO {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 转账单号
     */
    private String tradeNo;

    /**
     * 转账目的
     */
    private String purpose;

    /**
     * 转出账户
     */
    private String transferOutAccount;

    /**
     * 转入账户
     */
    private String transferToAccount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 交易时间
     */
    private Integer tradeTime;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 结算完成时间
     */
    private Integer settleFinishTime;


}
