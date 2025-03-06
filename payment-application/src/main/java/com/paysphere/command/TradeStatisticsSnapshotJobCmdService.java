package com.paysphere.command;

import com.paysphere.command.cmd.TradeStatisticsSnapshotJobCommand;

public interface TradeStatisticsSnapshotJobCmdService {

    void handlerTradeStatisticsSnapshot(TradeStatisticsSnapshotJobCommand command);
}
