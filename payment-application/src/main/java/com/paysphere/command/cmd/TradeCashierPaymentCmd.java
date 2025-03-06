package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class TradeCashierPaymentCmd {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 手机号码
     */
    private String phone;

}
