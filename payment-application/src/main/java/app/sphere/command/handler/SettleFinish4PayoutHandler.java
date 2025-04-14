package app.sphere.command.handler;

import app.sphere.command.cmd.SettleFinishCommand;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradePayoutOrderRepository;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import share.sphere.enums.SettleStatusEnum;

import javax.annotation.Resource;


@Slf4j
@Component
public class SettleFinish4PayoutHandler {

    @Resource
    TradePayoutOrderRepository tradePayoutOrderRepository;

    /**
     * 代收结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Payout(SettleFinishCommand command) {
        log.info("handlerSettleFinish4Payout command={}", JSONUtil.toJsonStr(command));
        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Payout statusEnum={}", statusEnum.name());

        //更新订单结算状态
        UpdateWrapper<TradePayoutOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(TradePayoutOrder::getSettleStatus, statusEnum.getCode())
                .set(TradePayoutOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradePayoutOrder::getSettleFinishTime, System.currentTimeMillis())
                .eq(TradePayoutOrder::getTradeNo, command.getTradeNo());
        boolean update = tradePayoutOrderRepository.update(updateWrapper);
        log.info("handlerSettleFinish4Payout update order result={}", update);
    }

}
