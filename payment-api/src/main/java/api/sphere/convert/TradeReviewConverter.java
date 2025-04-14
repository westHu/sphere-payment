package api.sphere.convert;


import app.sphere.command.cmd.TradeReviewCommand;
import api.sphere.controller.request.TradeReviewReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeReviewConverter {

    TradeReviewCommand convertTradeReviewCommand(TradeReviewReq req);
}
