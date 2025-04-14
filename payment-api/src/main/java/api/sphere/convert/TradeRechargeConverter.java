package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.TradeRechargeOrderPageVO;
import app.sphere.command.cmd.TradePreRechargeCommand;
import app.sphere.command.cmd.TradeRechargeCommand;
import app.sphere.query.param.TradeRechargeOrderPageParam;
import infrastructure.sphere.db.entity.TradeRechargeOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeRechargeConverter {

    TradePreRechargeCommand convertTradePreRechargeCommand(TradePreRechargeReq req);

    TradeRechargeCommand convertTradeRechargeCommand(TradeRechargeReq req);

    TradeRechargeOrderPageParam convertTradeRechargeOrderPageParam(TradeRechargeOrderPageReq req);

    List<TradeRechargeOrderPageVO> convertTradeRechargeOrderPageVOList(List<TradeRechargeOrder> records);
}
