package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.TradePayVO;
import api.sphere.controller.response.TradePayoutVO;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.TradePaymentDTO;
import app.sphere.command.dto.TradePayoutDTO;
import app.sphere.query.dto.SandboxTradePaymentLinkOrderPageDTO;
import app.sphere.query.param.*;
import infrastructure.sphere.db.entity.SandboxTradePaymentLinkOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SandboxTradeConverter {

    SandboxTradePaymentOrderPageParam convertSandboxTradePayOrderPageParam(SandboxTradePayOrderPageReq req);

    SandboxTradePayoutOrderPageParam convertSandboxTradeCashOrderPageParam(SandboxTradeCashOrderPageReq req);

    TradePaymentCmd convertTradePayCommand(TradePaymentReq req);

    TradeCashierPaymentCmd convertTradeCashierPayCommand(TradeCashierPaymentReq req);

    CashierParam convertCashierParam(CashierReq req);

    TradePayoutCommand convertTradeCashCommand(TradePayoutReq req);

    TradePaymentLinkCmd convertTradePaymentLinkCommand(TradePaymentLinkReq req);

    TradePaymentLinkPageParam convertTradePaymentLinkPageParam(TradePaymentLinkPageReq req);

    List<SandboxTradePaymentLinkOrderPageDTO> convertSandboxTradePaymentLinkOrderPageDTOList(List<SandboxTradePaymentLinkOrder> records);

    SandboxTradeForceSuccessCommand convertSandboxTradeForceSuccessCommand(SandboxTradeForceSuccessReq req);

    TradePayVO convertTradePayVO(TradePaymentDTO dto);

    TradePayoutVO convertTradeCashVO(TradePayoutDTO dto);
}
