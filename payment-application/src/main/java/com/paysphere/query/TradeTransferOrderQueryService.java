package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.TradeTransferOrder;
import com.paysphere.query.dto.TradeTransferOrderDTO;
import com.paysphere.query.param.TradeTransferOrderPageParam;

public interface TradeTransferOrderQueryService {

    Page<TradeTransferOrder> pageTransferOrderList(TradeTransferOrderPageParam param);

    String exportTransferOrderList(TradeTransferOrderPageParam param);

    TradeTransferOrderDTO getTransferOrderByTradeNo(String tradeNo);

}
