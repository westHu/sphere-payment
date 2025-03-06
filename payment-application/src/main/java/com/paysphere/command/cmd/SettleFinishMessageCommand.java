package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class SettleFinishMessageCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 结算单号
     */
    private String settleNo;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 结算状态
     */
    private boolean settleStatus;

    /**
     * 结算时间
     */
    private String settleTime;

}
