package com.paysphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.cache.RedisService;
import com.paysphere.command.cmd.SettleFinishMessageCommand;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.enums.SettleStatusEnum;
import com.paysphere.repository.TradePaymentOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Slf4j
@Component
public class SettleFinish4PayHandler {

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    RedisService redisService;

    /**
     * 收款结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Pay(SettleFinishMessageCommand command) {
        log.info("handlerSettleFinish4Pay command={}", JSONUtil.toJsonStr(command));
        String key = TradeConstant.LOCK_PREFIX_SETTLE_LISTENER + command.getTradeNo();
        redisService.lock(key, () -> doSettleFinish4Pay(command));
    }

    /**
     * 收款 结算完成操作
     */
    public boolean doSettleFinish4Pay(SettleFinishMessageCommand command) {
        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Pay statusEnum={}", statusEnum.name());

        // 更新订单结算状态
        LocalDateTime settleFinishTime = LocalDateTime.parse(command.getSettleTime(), TradeConstant.DF_0);
        UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
        payOrderUpdate.lambda()
                .set(TradePaymentOrder::getSettleStatus, statusEnum.getCode())
                .set(TradePaymentOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradePaymentOrder::getSettleFinishTime, settleFinishTime)
                .setSql(TradeConstant.VERSION_SQL)
                .eq(TradePaymentOrder::getTradeNo, command.getTradeNo());
        boolean update = tradePaymentOrderService.update(payOrderUpdate);
        log.info("handlerSettleFinish4Pay update order result={}", update);
        return update;
    }
}
