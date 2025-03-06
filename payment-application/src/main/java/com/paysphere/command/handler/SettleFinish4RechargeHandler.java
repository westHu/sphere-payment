package com.paysphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.cmd.SettleFinishMessageCommand;
import com.paysphere.db.entity.TradeRechargeOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.repository.TradeRechargeOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
public class SettleFinish4RechargeHandler {

    @Resource
    TradeRechargeOrderService tradeRechargeOrderService;
    @Resource
    RedisService redisService;

    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Recharge(SettleFinishMessageCommand command) {
        log.info("handlerSettleFinish4Recharge command={}", JSONUtil.toJsonStr(command));
        String key = TradeConstant.LOCK_PREFIX_SETTLE_LISTENER + command.getTradeNo();
        redisService.lock(key, () -> doSettleFinish4Recharge(command));
    }

    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    private boolean doSettleFinish4Recharge(SettleFinishMessageCommand command) {
        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Recharge statusEnum={}", statusEnum.name());

        // 更新订单结算状态
        LocalDateTime settleFinishTime = LocalDateTime.parse(command.getSettleTime(), TradeConstant.DF_0);
        UpdateWrapper<TradeRechargeOrder> rechargeOrderUpdate = new UpdateWrapper<>();
        rechargeOrderUpdate.lambda()
                .set(TradeRechargeOrder::getSettleStatus, statusEnum.getCode())
                .set(TradeRechargeOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradeRechargeOrder::getSettleFinishTime, settleFinishTime)
                .eq(TradeRechargeOrder::getTradeNo, command.getTradeNo());
        boolean update = tradeRechargeOrderService.update(rechargeOrderUpdate);
        log.info("handlerSettleFinish4Recharge update order result={}", update);
        return update;
    }
}
