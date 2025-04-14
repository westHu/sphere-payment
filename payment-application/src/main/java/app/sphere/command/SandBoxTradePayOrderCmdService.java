package app.sphere.command;

import app.sphere.command.cmd.*;
import app.sphere.command.dto.TradeCashierPaymentDTO;
import app.sphere.command.dto.TradePaymentDTO;

public interface SandBoxTradePayOrderCmdService {

    TradePaymentDTO executeSandBoxPay(TradePaymentCmd command);

    TradeCashierPaymentDTO executeSandboxCashierPay(TradeCashierPaymentCmd command);

    boolean sandboxPayForceSuccessOrFailed(SandboxTradeForceSuccessCommand command);

    String executeSandboxPaymentLink(TradePaymentLinkCmd command);
}
