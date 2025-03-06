package com.paysphere.command;

import com.paysphere.command.cmd.SandboxTradeForceSuccessCommand;
import com.paysphere.command.cmd.TradeCashierPaymentCmd;
import com.paysphere.command.cmd.TradePaymentCmd;
import com.paysphere.command.cmd.TradePaymentLinkCmd;
import com.paysphere.command.dto.TradeCashierPaymentDTO;
import com.paysphere.command.dto.TradePaymentDTO;

public interface SandBoxTradePayOrderCmdService {

    TradePaymentDTO executeSandBoxPay(TradePaymentCmd command);

    TradeCashierPaymentDTO executeSandboxCashierPay(TradeCashierPaymentCmd command);

    boolean sandboxPayForceSuccessOrFailed(SandboxTradeForceSuccessCommand command);

    String executeSandboxPaymentLink(TradePaymentLinkCmd command);
}
