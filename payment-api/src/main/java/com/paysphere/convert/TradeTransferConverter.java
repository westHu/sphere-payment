package com.paysphere.convert;


import com.paysphere.command.cmd.TradeTransferCommand;
import com.paysphere.controller.request.TradeTransferOrderPageReq;
import com.paysphere.controller.request.TradeTransferReq;
import com.paysphere.controller.response.TradeTransferOrderPageVO;
import com.paysphere.db.entity.TradeTransferOrder;
import com.paysphere.query.param.TradeTransferOrderPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeTransferConverter {

    TradeTransferCommand convertTradeTransferCommand(TradeTransferReq req);

    TradeTransferOrderPageParam convertTradeTransferOrderPageParam(TradeTransferOrderPageReq req);

    List<TradeTransferOrderPageVO> convertTradeTransferOrderPageVOList(List<TradeTransferOrder> records);
}
