package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class SettleJobCommand {

    private String batchNo;

    private String merchantId;

    private String startTradeTime;

    private String endTradeTime;
}
