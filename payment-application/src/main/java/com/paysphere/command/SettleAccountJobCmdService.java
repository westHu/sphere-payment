package com.paysphere.command;

import com.paysphere.command.cmd.SettleAccountFlowOrderRevisionJobCommand;
import com.paysphere.command.cmd.SettleAccountFlowRevisionJobCommand;
import com.paysphere.command.cmd.SettleAccountSnapshotJobCommand;

public interface SettleAccountJobCmdService {

    void accountDailySnapshot(SettleAccountSnapshotJobCommand command);

    void accountFlowRevision(SettleAccountFlowRevisionJobCommand command);

    void accountFlowOrderRevision(SettleAccountFlowOrderRevisionJobCommand command);
}
