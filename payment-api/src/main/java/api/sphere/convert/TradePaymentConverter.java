package api.sphere.convert;


import api.sphere.controller.request.*;
import app.sphere.command.cmd.*;
import app.sphere.query.param.*;
import org.mapstruct.*;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TradePaymentConverter {

    TradePaymentCmd convertTradePaymentCmd(TradePaymentReq req);

    TradePaymentOrderPageParam convertTradePaymentOrderPageParam(TradePayOrderPageReq req);

    TradeCashierPaymentCmd convertTradeCashierPaymentCmd(TradeCashierPaymentReq req);

    CashierParam convertCashierParam(CashierReq req);

    TradePaymentRefundCmd convertTradePaymentRefundCmd(TradePaymentRefundReq req);

    TradePaymentSupplementCmd convertTradePaymentSupplementCmd(TradePaymentSupplementReq req);

}
