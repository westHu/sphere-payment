package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantWithdrawConfigCommand {

    /**
     * 提现方式
     */
    private String withdrawPaymentMethod;

    /**
     * 提现方式名称
     */
    private String withdrawPaymentName;

    /**
     * 提现账户
     */
    private String withdrawAccount;

}
