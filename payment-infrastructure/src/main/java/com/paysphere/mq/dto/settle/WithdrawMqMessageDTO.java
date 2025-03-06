package com.paysphere.mq.dto.settle;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawMqMessageDTO {


    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 提现单号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private String tradeTime;

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
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal merchantFee;


}
