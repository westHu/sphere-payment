package api.sphere.convert;


import app.sphere.command.cmd.TradeCallbackCommand;
import api.sphere.controller.request.TradeCallbackReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeCallbackConverter {

    TradeCallbackCommand convertTradeCallbackCommand(TradeCallbackReq req);
}
