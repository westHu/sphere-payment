package app.sphere.query;

import app.sphere.query.dto.CashierDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.TradePayOrderDTO;
import app.sphere.query.dto.TradePayOrderPageDTO;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.TradePayOrderPageParam;
import app.sphere.query.param.TradePaymentLinkPageParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.TradePaymentLinkOrder;

public interface TradePaymentOrderQueryService {

    Page<TradePaymentLinkOrder> pagePaymentLinkList(TradePaymentLinkPageParam param);

    PageDTO<TradePayOrderPageDTO> pagePayOrderList(TradePayOrderPageParam param);

    String exportPayOrderList(TradePayOrderPageParam param);

    CashierDTO getCashier(CashierParam param);

    TradePayOrderDTO getPayOrderByTradeNo(String tradeNo);

}
