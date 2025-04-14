package app.sphere.command;

import app.sphere.command.cmd.SandboxTradeForceSuccessCommand;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.dto.TradePayoutDTO;

public interface SandBoxTradeCashOrderCmdService {

    TradePayoutDTO executeSandboxCash(TradePayoutCommand command);

    boolean sandboxCashForceSuccessOrFailed(SandboxTradeForceSuccessCommand command);

}
