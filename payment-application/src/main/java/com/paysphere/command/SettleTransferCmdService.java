package com.paysphere.command;

import com.paysphere.command.cmd.SettleTransferCommand;

public interface SettleTransferCmdService {

    void handlerTransfer(SettleTransferCommand command);
}
