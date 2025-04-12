package app.sphere.command;

import app.sphere.command.cmd.SandboxTradeForceSuccessCommand;
import app.sphere.command.cmd.TradeCashierPaymentCmd;
import app.sphere.command.cmd.TradePaymentCmd;
import app.sphere.command.cmd.TradePaymentLinkCmd;
import app.sphere.command.dto.TradeCashierPaymentDTO;
import app.sphere.command.dto.TradePaymentDTO;

public interface SandBoxTradePayOrderCmdService {

    TradePaymentDTO executeSandBoxPay(TradePaymentCmd command);

    TradeCashierPaymentDTO executeSandboxCashierPay(TradeCashierPaymentCmd command);

    boolean sandboxPayForceSuccessOrFailed(SandboxTradeForceSuccessCommand command);

    String executeSandboxPaymentLink(TradePaymentLinkCmd command);
}
