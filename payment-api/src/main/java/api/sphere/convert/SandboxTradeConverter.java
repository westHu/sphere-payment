package api.sphere.convert;


import app.sphere.command.cmd.SandboxTradeForceSuccessCommand;
import app.sphere.command.cmd.TradeCashierPaymentCmd;
import app.sphere.command.cmd.TradePaymentCmd;
import app.sphere.command.cmd.TradePaymentLinkCmd;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.dto.TradePaymentDTO;
import app.sphere.command.dto.TradePayoutDTO;
import api.sphere.controller.request.CashierReq;
import api.sphere.controller.request.SandboxTradeCashOrderPageReq;
import api.sphere.controller.request.SandboxTradeForceSuccessReq;
import api.sphere.controller.request.SandboxTradePayOrderPageReq;
import api.sphere.controller.request.TradeCashierPaymentReq;
import api.sphere.controller.request.TradePaymentLinkPageReq;
import api.sphere.controller.request.TradePaymentLinkReq;
import api.sphere.controller.request.TradePaymentReq;
import api.sphere.controller.request.TradePayoutReq;
import api.sphere.controller.response.TradePayVO;
import api.sphere.controller.response.TradePayoutVO;
import infrastructure.sphere.db.entity.SandboxTradePaymentLinkOrder;
import app.sphere.query.dto.SandboxTradePaymentLinkOrderPageDTO;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.SandboxTradeCashOrderPageParam;
import app.sphere.query.param.SandboxTradePayOrderPageParam;
import app.sphere.query.param.TradePaymentLinkPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SandboxTradeConverter {

    SandboxTradePayOrderPageParam convertSandboxTradePayOrderPageParam(SandboxTradePayOrderPageReq req);

    SandboxTradeCashOrderPageParam convertSandboxTradeCashOrderPageParam(SandboxTradeCashOrderPageReq req);

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
