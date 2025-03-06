package com.paysphere.command;

import com.paysphere.command.cmd.SettleFileJobCommand;

public interface SettleFileJobCmdService {

    void handlerSettleFile(SettleFileJobCommand command);
}
