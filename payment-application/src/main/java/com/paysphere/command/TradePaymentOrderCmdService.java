package com.paysphere.command;

import com.paysphere.command.cmd.TradeCashierPaymentCmd;
import com.paysphere.command.cmd.TradePaymentCmd;
import com.paysphere.command.cmd.TradePaymentLinkCmd;
import com.paysphere.command.cmd.TradePaymentRefundCmd;
import com.paysphere.command.cmd.TradePaymentSupplementCmd;
import com.paysphere.command.dto.TradeCashierPaymentDTO;
import com.paysphere.command.dto.TradePaymentDTO;

public interface TradePaymentOrderCmdService {

    String executePaymentLink(TradePaymentLinkCmd command);

    TradePaymentDTO executeApiPayment(TradePaymentCmd command);

    TradeCashierPaymentDTO executeCashierPay(TradeCashierPaymentCmd command);

    boolean executePaymentSupplement(TradePaymentSupplementCmd command);

    boolean executePaymentRefund(TradePaymentRefundCmd command);

}
