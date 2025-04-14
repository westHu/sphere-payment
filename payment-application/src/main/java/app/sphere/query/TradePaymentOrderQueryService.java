package app.sphere.query;

import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;

public interface TradePaymentOrderQueryService {

    Page<TradePaymentLinkOrder> pagePaymentLinkList(TradePaymentLinkPageParam param);

    PageDTO<TradePaymentOrderPageDTO> pagePaymentOrderList(TradePaymentOrderPageParam param);

    String exportPaymentOrderList(TradePaymentOrderPageParam param);

    CashierDTO getCashier(CashierParam param);

    TradePaymentOrderDTO getPaymentOrderByTradeNo(String tradeNo);

}
