package com.paysphere.convert;


import com.paysphere.command.cmd.TradePreRechargeCommand;
import com.paysphere.command.cmd.TradeRechargeCommand;
import com.paysphere.controller.request.TradePreRechargeReq;
import com.paysphere.controller.request.TradeRechargeOrderPageReq;
import com.paysphere.controller.request.TradeRechargeReq;
import com.paysphere.controller.response.TradeRechargeOrderPageVO;
import com.paysphere.db.entity.TradeRechargeOrder;
import com.paysphere.query.param.TradeRechargeOrderPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeRechargeConverter {

    TradePreRechargeCommand convertTradePreRechargeCommand(TradePreRechargeReq req);

    TradeRechargeCommand convertTradeRechargeCommand(TradeRechargeReq req);

    TradeRechargeOrderPageParam convertTradeRechargeOrderPageParam(TradeRechargeOrderPageReq req);

    List<TradeRechargeOrderPageVO> convertTradeRechargeOrderPageVOList(List<TradeRechargeOrder> records);
}
