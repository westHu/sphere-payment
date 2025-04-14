package app.sphere.query;

import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TradePaymentOrderQueryService {

    PageDTO<TradePaymentOrderPageDTO> pagePaymentOrderList(TradePaymentOrderPageParam param);

    String exportPaymentOrderList(TradePaymentOrderPageParam param);

    CashierDTO getCashier(CashierParam param);

    TradePaymentOrderDTO getPaymentOrderByTradeNo(String tradeNo);

}
