package app.sphere.command;

import app.sphere.command.cmd.*;

public interface SettleAccountJobCmdService {

    void accountDailySnapshot(SettleAccountSnapshotJobCommand command);

    void accountFlowRevision(SettleAccountFlowRevisionJobCommand command);

    void accountFlowOrderRevision(SettleAccountFlowOrderRevisionJobCommand command);
}
