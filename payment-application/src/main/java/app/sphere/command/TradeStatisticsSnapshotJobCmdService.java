package app.sphere.command;

import app.sphere.command.cmd.TradeStatisticsSnapshotJobCommand;

public interface TradeStatisticsSnapshotJobCmdService {

    void handlerTradeStatisticsSnapshot(TradeStatisticsSnapshotJobCommand command);
}
