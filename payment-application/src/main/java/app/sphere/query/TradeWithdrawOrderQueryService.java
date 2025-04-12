package app.sphere.query;

import app.sphere.query.dto.TradeWithdrawOrderDTO;
import app.sphere.query.param.TradeWithdrawOrderPageParam;
import app.sphere.query.param.WithdrawFlagParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;

public interface TradeWithdrawOrderQueryService {

    Page<TradeWithdrawOrder> pageWithdrawOrderList(TradeWithdrawOrderPageParam param);

    String exportWithdrawOrderList(TradeWithdrawOrderPageParam param);

    TradeWithdrawOrderDTO getWithdrawOrder(String tradeNo);

    boolean getWithdrawFlag(WithdrawFlagParam param);
}
