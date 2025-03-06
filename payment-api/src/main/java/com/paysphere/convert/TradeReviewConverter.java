package com.paysphere.convert;


import com.paysphere.command.cmd.TradeReviewCommand;
import com.paysphere.controller.request.TradeReviewReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeReviewConverter {

    TradeReviewCommand convertTradeReviewCommand(TradeReviewReq req);
}
