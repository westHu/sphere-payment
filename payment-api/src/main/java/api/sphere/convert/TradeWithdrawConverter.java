package api.sphere.convert;


import app.sphere.command.cmd.TradeWithdrawCommand;
import api.sphere.controller.request.TradeWithdrawOrderPageReq;
import api.sphere.controller.request.TradeWithdrawReq;
import api.sphere.controller.request.WithdrawFlagReq;
import api.sphere.controller.response.TradeWithdrawOrderPageVO;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import app.sphere.query.param.TradeWithdrawOrderPageParam;
import app.sphere.query.param.WithdrawFlagParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeWithdrawConverter {

    TradeWithdrawCommand convertTradeWithdrawCommand(TradeWithdrawReq req);

    TradeWithdrawOrderPageParam convertTradeWithdrawOrderPageParam(TradeWithdrawOrderPageReq req);

    List<TradeWithdrawOrderPageVO> convertTradeWithdrawOrderPageVOList(List<TradeWithdrawOrder> records);

    WithdrawFlagParam convertWithdrawFlagParam(WithdrawFlagReq req);
}
