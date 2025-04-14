package api.sphere.convert;


import api.sphere.controller.request.TradeCallbackReq;
import app.sphere.command.cmd.TradeCallbackCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeCallbackConverter {

    TradeCallbackCommand convertTradeCallbackCommand(TradeCallbackReq req);
}
