package api.sphere.convert;


import app.sphere.command.cmd.TradePreRechargeCommand;
import app.sphere.command.cmd.TradeRechargeCommand;
import api.sphere.controller.request.TradePreRechargeReq;
import api.sphere.controller.request.TradeRechargeOrderPageReq;
import api.sphere.controller.request.TradeRechargeReq;
import api.sphere.controller.response.TradeRechargeOrderPageVO;
import infrastructure.sphere.db.entity.TradeRechargeOrder;
import app.sphere.query.param.TradeRechargeOrderPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeRechargeConverter {

    TradePreRechargeCommand convertTradePreRechargeCommand(TradePreRechargeReq req);

    TradeRechargeCommand convertTradeRechargeCommand(TradeRechargeReq req);

    TradeRechargeOrderPageParam convertTradeRechargeOrderPageParam(TradeRechargeOrderPageReq req);

    List<TradeRechargeOrderPageVO> convertTradeRechargeOrderPageVOList(List<TradeRechargeOrder> records);
}
