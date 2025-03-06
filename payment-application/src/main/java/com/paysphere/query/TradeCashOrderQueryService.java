package com.paysphere.query;

import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.TradeCashOrderDTO;
import com.paysphere.query.dto.TradeCashOrderPageDTO;
import com.paysphere.query.dto.TradeCashReceiptDTO;
import com.paysphere.query.param.TradeCashOrderPageParam;

public interface TradeCashOrderQueryService {

    PageDTO<TradeCashOrderPageDTO> pageCashOrderList(TradeCashOrderPageParam param);

    String exportCashOrderList(TradeCashOrderPageParam param);

    TradeCashOrderDTO getCashOrderByTradeNo(String tradeNo);

    TradeCashReceiptDTO getCashReceipt(String tradeNo);

}
