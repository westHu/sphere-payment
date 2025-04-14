package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.TradePayVO;
import api.sphere.controller.response.TradePayoutVO;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.TradePaymentDTO;
import app.sphere.command.dto.TradePayoutDTO;
import app.sphere.query.param.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface SandboxTradeConverter {

    SandboxTradePaymentOrderPageParam convertSandboxTradePayOrderPageParam(SandboxTradePayOrderPageReq req);

    SandboxTradePayoutOrderPageParam convertSandboxTradeCashOrderPageParam(SandboxTradeCashOrderPageReq req);

    @Mapping(target = "tradePaySource", ignore = true)
    TradePaymentCmd convertTradePayCommand(TradePaymentReq req);

    TradeCashierPaymentCmd convertTradeCashierPayCommand(TradeCashierPaymentReq req);

    CashierParam convertCashierParam(CashierReq req);

    @Mapping(target = "tradePayoutSourceEnum", ignore = true)
    TradePayoutCommand convertTradePayoutCommand(TradePayoutReq req);

    SandboxTradeForceSuccessCommand convertSandboxTradeForceSuccessCommand(SandboxTradeForceSuccessReq req);

    TradePayVO convertTradePayVO(TradePaymentDTO dto);

    TradePayoutVO convertTradeCashVO(TradePayoutDTO dto);
}
