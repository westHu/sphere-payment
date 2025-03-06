package com.paysphere.command;

import com.paysphere.command.cmd.TradeFileJobCommand;

public interface TradeFileJobCmdService {

    void handlerTradeFile(TradeFileJobCommand command);
}
