package com.paysphere.convert;


import com.paysphere.command.cmd.TradeWithdrawCommand;
import com.paysphere.controller.request.TradeWithdrawOrderPageReq;
import com.paysphere.controller.request.TradeWithdrawReq;
import com.paysphere.controller.request.WithdrawFlagReq;
import com.paysphere.controller.response.TradeWithdrawOrderPageVO;
import com.paysphere.db.entity.TradeWithdrawOrder;
import com.paysphere.query.param.TradeWithdrawOrderPageParam;
import com.paysphere.query.param.WithdrawFlagParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeWithdrawConverter {

    TradeWithdrawCommand convertTradeWithdrawCommand(TradeWithdrawReq req);

    TradeWithdrawOrderPageParam convertTradeWithdrawOrderPageParam(TradeWithdrawOrderPageReq req);

    List<TradeWithdrawOrderPageVO> convertTradeWithdrawOrderPageVOList(List<TradeWithdrawOrder> records);

    WithdrawFlagParam convertWithdrawFlagParam(WithdrawFlagReq req);
}
