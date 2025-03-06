package com.paysphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.cmd.SettleFinishMessageCommand;
import com.paysphere.db.entity.TradeWithdrawOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.repository.TradeWithdrawOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
public class SettleFinish4WithdrawHandler {

    @Resource
    TradeWithdrawOrderService tradeWithdrawOrderService;
    @Resource
    RedisService redisService;

    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Withdraw(SettleFinishMessageCommand command) {
        log.info("handlerSettleFinish4Withdraw command={}", JSONUtil.toJsonStr(command));
        String key = TradeConstant.LOCK_PREFIX_SETTLE_LISTENER + command.getTradeNo();
        redisService.lock(key, () -> doSettleFinish4Withdraw(command));
    }

    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    private boolean doSettleFinish4Withdraw(SettleFinishMessageCommand command) {
        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Withdraw statusEnum={}", statusEnum.name());

        // 更新订单结算状态
        LocalDateTime settleFinishTime = LocalDateTime.parse(command.getSettleTime(), TradeConstant.DF_0);
        UpdateWrapper<TradeWithdrawOrder> withdrawOrderUpdate = new UpdateWrapper<>();
        withdrawOrderUpdate.lambda()
                .set(TradeWithdrawOrder::getSettleStatus, statusEnum.getCode())
                .set(TradeWithdrawOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradeWithdrawOrder::getSettleFinishTime, settleFinishTime)
                .eq(TradeWithdrawOrder::getTradeNo, command.getTradeNo());
        boolean update = tradeWithdrawOrderService.update(withdrawOrderUpdate);
        log.info("handlerSettleFinish4Withdraw update order result={}", update);
        return update;
    }
}
