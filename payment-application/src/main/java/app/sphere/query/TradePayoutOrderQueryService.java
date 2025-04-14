package app.sphere.query;

import app.sphere.query.dto.*;
import app.sphere.query.param.TradePayoutOrderPageParam;

public interface TradePayoutOrderQueryService {

    PageDTO<TradePayoutOrderPageDTO> pagePayoutOrderList(TradePayoutOrderPageParam param);

    String exportPayoutOrderList(TradePayoutOrderPageParam param);

    TradePayoutOrderDTO getPayoutOrderByTradeNo(String tradeNo);

    TradePayoutReceiptDTO getPayoutReceipt(String tradeNo);

}
