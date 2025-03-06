package com.paysphere.command;

import com.paysphere.command.cmd.SandboxTradeForceSuccessCommand;
import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.dto.TradePayoutDTO;

public interface SandBoxTradeCashOrderCmdService {

    TradePayoutDTO executeSandboxCash(TradeCashCommand command);

    boolean sandboxCashForceSuccessOrFailed(SandboxTradeForceSuccessCommand command);

}
