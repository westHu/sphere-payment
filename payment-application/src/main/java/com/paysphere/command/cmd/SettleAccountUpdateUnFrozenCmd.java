package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class SettleAccountUpdateUnFrozenCmd {

    /**
     * 交易单号
     */
    private String tradeNo;


    /**
     * 外部订单号
     */
    private String outerNo;

}
