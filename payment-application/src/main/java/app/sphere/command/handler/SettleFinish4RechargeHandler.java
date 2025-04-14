package app.sphere.command.handler;

import app.sphere.command.cmd.SettleFinishCommand;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradeRechargeOrderRepository;
import infrastructure.sphere.db.entity.TradeRechargeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import share.sphere.enums.SettleStatusEnum;

import javax.annotation.Resource;


@Slf4j
@Component
public class SettleFinish4RechargeHandler {

    @Resource
    TradeRechargeOrderRepository tradeRechargeOrderRepository;


    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Recharge(SettleFinishCommand command) {
        log.info("handlerSettleFinish4Recharge command={}", JSONUtil.toJsonStr(command));

        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Recharge statusEnum={}", statusEnum.name());

        //更新订单结算状态
        UpdateWrapper<TradeRechargeOrder> rechargeOrderUpdate = new UpdateWrapper<>();
        rechargeOrderUpdate.lambda()
                .set(TradeRechargeOrder::getSettleStatus, statusEnum.getCode())
                .set(TradeRechargeOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradeRechargeOrder::getSettleFinishTime, System.currentTimeMillis())
                .eq(TradeRechargeOrder::getTradeNo, command.getTradeNo());
        boolean update = tradeRechargeOrderRepository.update(rechargeOrderUpdate);
        log.info("handlerSettleFinish4Recharge update order result={}", update);
    }
}
