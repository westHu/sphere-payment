package api.sphere.convert;


import api.sphere.controller.request.TradeTransferOrderPageReq;
import api.sphere.controller.request.TradeTransferReq;
import api.sphere.controller.response.TradeTransferOrderPageVO;
import app.sphere.command.cmd.TradeTransferCommand;
import app.sphere.query.param.TradeTransferOrderPageParam;
import infrastructure.sphere.db.entity.TradeTransferOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeTransferConverter {

    TradeTransferCommand convertTradeTransferCommand(TradeTransferReq req);

    TradeTransferOrderPageParam convertTradeTransferOrderPageParam(TradeTransferOrderPageReq req);

    List<TradeTransferOrderPageVO> convertTradeTransferOrderPageVOList(List<TradeTransferOrder> records);
}
