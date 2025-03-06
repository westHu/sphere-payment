package com.paysphere.command.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PreRechargeDTO {

    /**
     * 充值单号
     */
    private String tradeNo;

    /**
     * 持卡人姓名
     */
    private String holderName;

    /**
     * 充值账户
     */
    private String bankAccount;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 充值随机金额
     */
    private BigDecimal randomAmount;

}
