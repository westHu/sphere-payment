package com.paysphere.mq.dto.email;

import lombok.Data;

@Data
public class EmailTradePayinReceiptDTO {

    /**
     * 单号
     */
    private String tradeNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 金额
     */
    private String amount;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 交易时间
     */
    private String transactionTime;

    /**
     * 备注
     */
    private String remark;

}