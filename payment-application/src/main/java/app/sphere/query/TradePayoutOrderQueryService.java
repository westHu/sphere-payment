package app.sphere.query;

import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.TradeCashOrderDTO;
import app.sphere.query.dto.TradeCashOrderPageDTO;
import app.sphere.query.dto.TradeCashReceiptDTO;
import app.sphere.query.param.TradeCashOrderPageParam;

public interface TradePayoutOrderQueryService {

    PageDTO<TradeCashOrderPageDTO> pageCashOrderList(TradeCashOrderPageParam param);

    String exportCashOrderList(TradeCashOrderPageParam param);

    TradeCashOrderDTO getCashOrderByTradeNo(String tradeNo);

    TradeCashReceiptDTO getCashReceipt(String tradeNo);

}
