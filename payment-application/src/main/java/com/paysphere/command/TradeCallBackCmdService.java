package com.paysphere.command;

import com.paysphere.command.cmd.TradeCallbackCmd;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;

public interface TradeCallBackCmdService {

    boolean handlerTradeCallback(TradeCallBackDTO callBackDTO);

    boolean handlerTradeCallback(TradeCallbackCmd command);
}
