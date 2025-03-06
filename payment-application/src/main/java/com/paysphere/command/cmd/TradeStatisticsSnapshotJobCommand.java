package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class TradeStatisticsSnapshotJobCommand {

    /**
     * 交易日期
     */
    private String tradeDate;
}
