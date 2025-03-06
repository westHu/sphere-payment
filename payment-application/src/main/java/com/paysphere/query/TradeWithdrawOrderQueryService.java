package com.paysphere.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.db.entity.TradeWithdrawOrder;
import com.paysphere.query.dto.TradeWithdrawOrderDTO;
import com.paysphere.query.param.TradeWithdrawOrderPageParam;
import com.paysphere.query.param.WithdrawFlagParam;

public interface TradeWithdrawOrderQueryService {

    Page<TradeWithdrawOrder> pageWithdrawOrderList(TradeWithdrawOrderPageParam param);

    String exportWithdrawOrderList(TradeWithdrawOrderPageParam param);

    TradeWithdrawOrderDTO getWithdrawOrder(String tradeNo);

    boolean getWithdrawFlag(WithdrawFlagParam param);
}
