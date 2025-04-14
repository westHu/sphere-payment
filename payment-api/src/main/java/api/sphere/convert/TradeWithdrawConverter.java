package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.TradeWithdrawOrderPageVO;
import app.sphere.command.cmd.TradeWithdrawCommand;
import app.sphere.query.param.TradeWithdrawOrderPageParam;
import app.sphere.query.param.WithdrawFlagParam;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeWithdrawConverter {

    TradeWithdrawCommand convertTradeWithdrawCommand(TradeWithdrawReq req);

    TradeWithdrawOrderPageParam convertTradeWithdrawOrderPageParam(TradeWithdrawOrderPageReq req);

    List<TradeWithdrawOrderPageVO> convertTradeWithdrawOrderPageVOList(List<TradeWithdrawOrder> records);

    WithdrawFlagParam convertWithdrawFlagParam(TradeWithdrawFlagReq req);
}
