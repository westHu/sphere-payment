package com.paysphere.command;

import com.paysphere.command.cmd.TradePayOrderTimeOutJobCommand;

public interface TradeJobCmdService {

    void handlerTimeOut(TradePayOrderTimeOutJobCommand command);


}
