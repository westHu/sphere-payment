package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDailySnapchatDTO {

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 转账方向
     */
    private Integer transferDirection;

    /**
     * 币种
     */
    private String currency;

    /**
     * 订单成功金额
     */
    private BigDecimal orderSuccessAmount;

}
