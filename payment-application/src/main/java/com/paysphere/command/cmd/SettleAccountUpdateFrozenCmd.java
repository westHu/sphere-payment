package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleAccountUpdateFrozenCmd {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 外部订单号
     */
    private String outerNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 冻结金额
     */
    private BigDecimal amount;

    /**
     * 商户信息
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户账户号
     */
    private String accountNo;

}
