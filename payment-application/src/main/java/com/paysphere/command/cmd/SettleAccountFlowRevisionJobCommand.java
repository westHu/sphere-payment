package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class SettleAccountFlowRevisionJobCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户号
     */
    private String accountNo;
}
