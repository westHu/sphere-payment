package app.sphere.query;

import app.sphere.query.param.TradeRechargeOrderPageParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.TradeRechargeOrder;

public interface TradeRechargeOrderQueryService {

    Page<TradeRechargeOrder> pageRechargeOrderList(TradeRechargeOrderPageParam param);

    String exportRechargeOrderList(TradeRechargeOrderPageParam param);
}
