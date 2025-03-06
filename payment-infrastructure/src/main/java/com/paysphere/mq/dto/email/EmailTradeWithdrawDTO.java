package com.paysphere.mq.dto.email;

import lombok.Data;

@Data
public class EmailTradeWithdrawDTO {

    /**
     * paysphere账户
     */
    private String accountNo;

    /**
     * 银行账户
     */
    private String bankAccountNo;

    /**
     * 转账金额
     */
    private String withdrawAmount;

    /**
     * 手续费
     */
    private String fee;

    /**
     * 目的
     */
    private String purpose;
}
