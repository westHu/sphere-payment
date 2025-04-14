package app.sphere.command;

import app.sphere.command.cmd.SettlePayoutCommand;

public interface SettlePayoutCmdService {

    void handlerSettleImmediate(SettlePayoutCommand command);

}
