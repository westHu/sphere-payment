package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class TradeCashRefundCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 操作员
     */
    private String operator;

}
