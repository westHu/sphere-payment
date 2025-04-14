package api.sphere.convert;


import api.sphere.controller.request.TradeReviewReq;
import app.sphere.command.cmd.TradeReviewCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeReviewConverter {

    TradeReviewCommand convertTradeReviewCommand(TradeReviewReq req);
}
