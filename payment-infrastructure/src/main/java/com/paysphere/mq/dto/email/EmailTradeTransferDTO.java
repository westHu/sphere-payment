package com.paysphere.mq.dto.email;

import lombok.Data;

@Data
public class EmailTradeTransferDTO {

    /**
     * 转账转出
     */
    private String fromAccountNo;

    /**
     * 转账转入
     */
    private String toAccountNo;

    /**
     * 转账金额
     */
    private String transferAmount;

    /**
     * 手续费
     */
    private String fee;

    /**
     * 目的
     */
    private String purpose;
}
