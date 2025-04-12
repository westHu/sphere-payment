package app.sphere.query;

import app.sphere.query.dto.TradeTransferOrderDTO;
import app.sphere.query.param.TradeTransferOrderPageParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.TradeTransferOrder;

public interface TradeTransferOrderQueryService {

    Page<TradeTransferOrder> pageTransferOrderList(TradeTransferOrderPageParam param);

    String exportTransferOrderList(TradeTransferOrderPageParam param);

    TradeTransferOrderDTO getTransferOrderByTradeNo(String tradeNo);

}
