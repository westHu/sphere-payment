package app.sphere.command;

import app.sphere.command.cmd.SettleAccountFlowOrderRevisionJobCommand;
import app.sphere.command.cmd.SettleAccountFlowRevisionJobCommand;
import app.sphere.command.cmd.SettleAccountSnapshotJobCommand;

public interface SettleAccountJobCmdService {

    void accountDailySnapshot(SettleAccountSnapshotJobCommand command);

    void accountFlowRevision(SettleAccountFlowRevisionJobCommand command);

    void accountFlowOrderRevision(SettleAccountFlowOrderRevisionJobCommand command);
}
