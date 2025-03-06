package com.paysphere.command;

import com.paysphere.command.cmd.SettleWithdrawCommand;

public interface SettleWithdrawCmdService {

    void handlerWithdraw(SettleWithdrawCommand command);

}
