package app.sphere.command;

import app.sphere.command.cmd.TradeFileJobCommand;

public interface TradeFileJobCmdService {

    void handlerTradeFile(TradeFileJobCommand command);
}
