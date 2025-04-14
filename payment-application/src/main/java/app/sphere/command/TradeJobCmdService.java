package app.sphere.command;

import app.sphere.command.cmd.TradePayOrderTimeOutJobCommand;

public interface TradeJobCmdService {

    void handlerTimeOut(TradePayOrderTimeOutJobCommand command);


}
