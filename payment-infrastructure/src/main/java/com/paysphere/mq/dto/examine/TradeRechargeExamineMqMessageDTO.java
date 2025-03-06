package com.paysphere.mq.dto.examine;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRechargeExamineMqMessageDTO {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 充值单号
     */
    private String tradeNo;

    /**
     * 充值时间
     */
    private String tradeTime;

    /**
     * 充值目的
     */
    private String purpose;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
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
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 充值账户
     */
    private String bankAccount;

    /**
     * 证明 譬如图片截图
     */
    private String proof;

}
