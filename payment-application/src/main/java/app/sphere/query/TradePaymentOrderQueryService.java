package app.sphere.query;

import app.sphere.query.dto.CashierDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.TradePaymentOrderDTO;
import app.sphere.query.dto.TradePaymentOrderPageDTO;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.TradePaymentOrderPageParam;
import app.sphere.query.param.TradePaymentLinkPageParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;

public interface TradePaymentOrderQueryService {

    Page<TradePaymentLinkOrder> pagePaymentLinkList(TradePaymentLinkPageParam param);

    PageDTO<TradePaymentOrderPageDTO> pagePaymentOrderList(TradePaymentOrderPageParam param);

    String exportPaymentOrderList(TradePaymentOrderPageParam param);

    CashierDTO getCashier(CashierParam param);

    TradePaymentOrderDTO getPaymentOrderByTradeNo(String tradeNo);

}
