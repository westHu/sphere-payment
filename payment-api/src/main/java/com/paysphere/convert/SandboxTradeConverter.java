package com.paysphere.convert;


import com.paysphere.command.cmd.SandboxTradeForceSuccessCommand;
import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.cmd.TradeCashierPaymentCmd;
import com.paysphere.command.cmd.TradePaymentCmd;
import com.paysphere.command.cmd.TradePaymentLinkCmd;
import com.paysphere.command.dto.TradePaymentDTO;
import com.paysphere.command.dto.TradePayoutDTO;
import com.paysphere.controller.request.CashierReq;
import com.paysphere.controller.request.SandboxTradeCashOrderPageReq;
import com.paysphere.controller.request.SandboxTradeForceSuccessReq;
import com.paysphere.controller.request.SandboxTradePayOrderPageReq;
import com.paysphere.controller.request.TradeCashReq;
import com.paysphere.controller.request.TradeCashierPaymentReq;
import com.paysphere.controller.request.TradePaymentLinkPageReq;
import com.paysphere.controller.request.TradePaymentLinkReq;
import com.paysphere.controller.request.TradePaymentReq;
import com.paysphere.controller.response.TradeCashVO;
import com.paysphere.controller.response.TradePayVO;
import com.paysphere.db.entity.SandboxTradePaymentLinkOrder;
import com.paysphere.query.dto.SandboxTradePaymentLinkOrderPageDTO;
import com.paysphere.query.param.CashierParam;
import com.paysphere.query.param.SandboxTradeCashOrderPageParam;
import com.paysphere.query.param.SandboxTradePayOrderPageParam;
import com.paysphere.query.param.TradePaymentLinkPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface SandboxTradeConverter {

    SandboxTradePayOrderPageParam convertSandboxTradePayOrderPageParam(SandboxTradePayOrderPageReq req);

    SandboxTradeCashOrderPageParam convertSandboxTradeCashOrderPageParam(SandboxTradeCashOrderPageReq req);

    TradePaymentCmd convertTradePayCommand(TradePaymentReq req);

    TradeCashierPaymentCmd convertTradeCashierPayCommand(TradeCashierPaymentReq req);

    CashierParam convertCashierParam(CashierReq req);

    TradeCashCommand convertTradeCashCommand(TradeCashReq req);

    TradePaymentLinkCmd convertTradePaymentLinkCommand(TradePaymentLinkReq req);

    TradePaymentLinkPageParam convertTradePaymentLinkPageParam(TradePaymentLinkPageReq req);

    List<SandboxTradePaymentLinkOrderPageDTO> convertSandboxTradePaymentLinkOrderPageDTOList(List<SandboxTradePaymentLinkOrder> records);

    SandboxTradeForceSuccessCommand convertSandboxTradeForceSuccessCommand(SandboxTradeForceSuccessReq req);

    TradePayVO convertTradePayVO(TradePaymentDTO dto);

    TradeCashVO convertTradeCashVO(TradePayoutDTO dto);
}
