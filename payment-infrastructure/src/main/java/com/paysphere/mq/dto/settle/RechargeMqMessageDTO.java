package com.paysphere.mq.dto.settle;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeMqMessageDTO {


    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 充值单号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private String tradeTime;

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

}
