package com.paysphere.convert;


import com.paysphere.command.cmd.TradeCallbackCmd;
import com.paysphere.controller.request.TradeCallbackReq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface TradeCallbackConverter {

    TradeCallbackCmd convertTradeCallbackCmd(TradeCallbackReq req);
}
