package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class TradeCallbackCmd {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 操作人
     */
    private String operator;

}
