package app.sphere.command;

import app.sphere.command.cmd.*;
import app.sphere.command.dto.TradeCashierPaymentDTO;
import app.sphere.command.dto.TradePaymentDTO;

public interface TradePaymentOrderCmdService {

    String executePaymentLink(TradePaymentCmd command);

    TradePaymentDTO executeApiPayment(TradePaymentCmd command);

    TradeCashierPaymentDTO executeCashierPay(TradeCashierPaymentCmd command);

    boolean executePaymentSupplement(TradePaymentSupplementCmd command);

    boolean executePaymentRefund(TradePaymentRefundCmd command);

}
