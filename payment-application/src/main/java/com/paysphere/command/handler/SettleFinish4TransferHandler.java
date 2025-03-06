package com.paysphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.cmd.SettleFinishMessageCommand;
import com.paysphere.db.entity.TradeTransferOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.repository.TradeTransferOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
public class SettleFinish4TransferHandler {

    @Resource
    TradeTransferOrderService tradeTransferOrderService;
    @Resource
    RedisService redisService;

    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Transfer(SettleFinishMessageCommand command) {
        log.info("handlerSettleFinish4Transfer command={}", JSONUtil.toJsonStr(command));
        String key = TradeConstant.LOCK_PREFIX_SETTLE_LISTENER + command.getTradeNo();
        redisService.lock(key, () -> doSettleFinish4Transfer(command));
    }

    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    private boolean doSettleFinish4Transfer(SettleFinishMessageCommand command) {
        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Transfer statusEnum={}", statusEnum.name());

        // 更新订单结算状态
        LocalDateTime settleFinishTime = LocalDateTime.parse(command.getSettleTime(), TradeConstant.DF_0);
        UpdateWrapper<TradeTransferOrder> transferOrderUpdate = new UpdateWrapper<>();
        transferOrderUpdate.lambda()
                .set(TradeTransferOrder::getSettleStatus, statusEnum.getCode())
                .set(TradeTransferOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradeTransferOrder::getSettleFinishTime, settleFinishTime)
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradeTransferOrder::getTradeNo, command.getTradeNo());
        boolean update = tradeTransferOrderService.update(transferOrderUpdate);
        log.info("handlerSettleFinish4Transfer update order result={}", update);
        return update;
    }
}
