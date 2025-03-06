package com.paysphere.convert;


import com.paysphere.command.cmd.TradeCashierPaymentCmd;
import com.paysphere.command.cmd.TradePaymentCmd;
import com.paysphere.command.cmd.TradePaymentLinkCmd;
import com.paysphere.command.cmd.TradePaymentRefundCmd;
import com.paysphere.command.cmd.TradePaymentSupplementCmd;
import com.paysphere.command.dto.TradeCashierPaymentDTO;
import com.paysphere.command.dto.TradePaymentDTO;
import com.paysphere.controller.request.CashierReq;
import com.paysphere.controller.request.TradeCashierPaymentReq;
import com.paysphere.controller.request.TradePayOrderPageReq;
import com.paysphere.controller.request.TradePaymentLinkPageReq;
import com.paysphere.controller.request.TradePaymentLinkReq;
import com.paysphere.controller.request.TradePaymentRefundReq;
import com.paysphere.controller.request.TradePaymentReq;
import com.paysphere.controller.request.TradePaymentSupplementReq;
import com.paysphere.controller.response.TradeCashierPayVO;
import com.paysphere.controller.response.TradeCashierVO;
import com.paysphere.controller.response.TradePayVO;
import com.paysphere.controller.response.TradePaymentLinkOrderVO;
import com.paysphere.db.entity.TradePaymentLinkOrder;
import com.paysphere.query.dto.CashierDTO;
import com.paysphere.query.param.CashierParam;
import com.paysphere.query.param.TradePayOrderPageParam;
import com.paysphere.query.param.TradePaymentLinkPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradePayConverter {

    TradePaymentCmd convertTradePayCmd(TradePaymentReq req);

    TradePayVO convertTradePayVO(TradePaymentDTO dto);

    TradePayOrderPageParam convertPageParam(TradePayOrderPageReq req);

    TradeCashierPaymentCmd convertTradeCashierPaymentCmd(TradeCashierPaymentReq req);

    CashierParam convertCashierParam(CashierReq req);

    TradeCashierPayVO convertTradeCashierPayVO(TradeCashierPaymentDTO dto);

    TradeCashierVO convertCashierVO(CashierDTO dto);

    TradePaymentRefundCmd convertTradePaymentRefundCmd(TradePaymentRefundReq req);

    TradePaymentSupplementCmd convertTradePaySupplementCmd(TradePaymentSupplementReq req);

    TradePaymentLinkCmd convertTradePaymentLinkCmd(TradePaymentLinkReq req);

    TradePaymentLinkPageParam convertTradePaymentLinkPageParam(TradePaymentLinkPageReq req);

    TradePaymentLinkOrderVO convertTradePaymentLinkOrderVOList(TradePaymentLinkOrder record);

    List<TradePaymentLinkOrderVO> convertTradePaymentLinkOrderVOList(List<TradePaymentLinkOrder> records);
}
