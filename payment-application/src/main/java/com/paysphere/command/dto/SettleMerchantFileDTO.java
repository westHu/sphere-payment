package com.paysphere.command.dto;

import lombok.Data;

@Data
public class SettleMerchantFileDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户交易单号
     */
    private String merchantOrderNo;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 时间
     */
    private String tradeTime;

    /**
     * 结算时间
     */
    private String actualSettleTime;

    /**
     * 金额
     */
    private String amount;

}
