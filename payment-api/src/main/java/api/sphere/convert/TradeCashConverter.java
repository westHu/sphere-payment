package api.sphere.convert;


import app.sphere.command.cmd.TradeCashRefundCommand;
import app.sphere.command.cmd.TradeCashSupplementCommand;
import app.sphere.command.cmd.TradePayoutCommand;
import app.sphere.command.dto.TradePayoutDTO;
import api.sphere.controller.request.TradeCashOrderPageReq;
import api.sphere.controller.request.TradeCashRefundReq;
import api.sphere.controller.request.TradeCashSupplementReq;
import api.sphere.controller.request.TradePayoutReq;
import api.sphere.controller.response.TradeCashOrderPageVO;
import api.sphere.controller.response.TradePayoutVO;
import app.sphere.query.dto.TradePayoutOrderPageDTO;
import app.sphere.query.param.TradePayoutOrderPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradeCashConverter {

    TradePayoutCommand convertTradePayoutCommand(TradePayoutReq req);

    TradePayoutVO convertTradePayoutVO(TradePayoutDTO dto);

    TradePayoutOrderPageParam convertPageParam(TradeCashOrderPageReq req);

    List<TradeCashOrderPageVO> convertPageVOList(List<TradePayoutOrderPageDTO> orderList);

    TradeCashSupplementCommand convertTradeCashSupplementCommand(TradeCashSupplementReq req);

    TradeCashRefundCommand convertTradeCashRefundCommand(TradeCashRefundReq req);

    TradePayoutOrderPageParam convertTradeCashOrderPageParam(TradeCashOrderPageReq req);

    List<TradeCashOrderPageVO> convertTradeCashOrderPageVOList(List<TradePayoutOrderPageDTO> data);
}
