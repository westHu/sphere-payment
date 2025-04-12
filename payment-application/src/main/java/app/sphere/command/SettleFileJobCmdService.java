package app.sphere.command;

import app.sphere.command.cmd.SettleFileJobCommand;

public interface SettleFileJobCmdService {

    void handlerSettleFile(SettleFileJobCommand command);
}
