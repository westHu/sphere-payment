package app.sphere.command;

import app.sphere.command.cmd.SettleWithdrawCommand;

public interface SettleWithdrawCmdService {

    void handlerWithdraw(SettleWithdrawCommand command);

}
