package com.paysphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.cmd.SettleFinishMessageCommand;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.repository.TradePayoutOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SettleFinish4CashHandler {

    @Resource
    TradePayoutOrderService tradePayoutOrderService;
    @Resource
    RedisService redisService;

    /**
     * 收款结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Cash(SettleFinishMessageCommand command) {
        log.info("handlerSettleFinish4Cash command={}", JSONUtil.toJsonStr(command));
        String key = TradeConstant.LOCK_PREFIX_SETTLE_LISTENER + command.getTradeNo();
        redisService.lock(key, () -> doSettleFinish4Cash(command));
    }


    /**
     * 收款结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    private boolean doSettleFinish4Cash(SettleFinishMessageCommand command) {
        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Cash statusEnum={}", statusEnum.name());

        // 更新订单结算状态
        LocalDateTime settleFinishTime = LocalDateTime.parse(command.getSettleTime(), TradeConstant.DF_0);
        UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
        cashOrderUpdate.lambda()
                .set(TradePayoutOrder::getSettleStatus, statusEnum.getCode())
                .set(TradePayoutOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradePayoutOrder::getSettleFinishTime, settleFinishTime)
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePayoutOrder::getTradeNo, command.getTradeNo());
        boolean update = tradePayoutOrderService.update(cashOrderUpdate);
        log.info("handlerSettleFinish4Cash update order result={}", update);
        return update;
    }
}
