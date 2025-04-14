package api.sphere.convert;


import api.sphere.controller.request.*;
import api.sphere.controller.response.TradeCashOrderPageVO;
import api.sphere.controller.response.TradePayoutVO;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.TradePayoutDTO;
import app.sphere.query.dto.TradePayoutOrderPageDTO;
import app.sphere.query.param.TradePayoutOrderPageParam;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface TradePayoutConverter {

    TradePayoutCommand convertTradePayoutCommand(TradePayoutReq req);

    TradePayoutVO convertTradePayoutVO(TradePayoutDTO dto);

    TradePayoutOrderPageParam convertPageParam(TradePayoutOrderPageReq req);

    List<TradeCashOrderPageVO> convertPageVOList(List<TradePayoutOrderPageDTO> orderList);

    TradeCashSupplementCommand convertTradeCashSupplementCommand(TradeCashSupplementReq req);

    TradeCashRefundCommand convertTradeCashRefundCommand(TradeCashRefundReq req);

    TradePayoutOrderPageParam convertTradeCashOrderPageParam(TradePayoutOrderPageReq req);

    List<TradeCashOrderPageVO> convertTradeCashOrderPageVOList(List<TradePayoutOrderPageDTO> data);
}
