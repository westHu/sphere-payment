package app.sphere.command;

import app.sphere.command.cmd.SettleTransferCommand;

public interface SettleTransferCmdService {

    void handlerTransfer(SettleTransferCommand command);
}
