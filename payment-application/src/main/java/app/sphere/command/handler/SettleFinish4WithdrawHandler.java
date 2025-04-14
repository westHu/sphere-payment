package app.sphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import app.sphere.command.cmd.SettleFinishCommand;
import infrastructure.sphere.db.entity.TradeWithdrawOrder;
import share.sphere.enums.SettleStatusEnum;
import domain.sphere.repository.TradeWithdrawOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Slf4j
@Component
public class SettleFinish4WithdrawHandler {

    @Resource
    TradeWithdrawOrderRepository tradeWithdrawOrderRepository;


    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Withdraw(SettleFinishCommand command) {
        log.info("handlerSettleFinish4Withdraw command={}", JSONUtil.toJsonStr(command));

        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Withdraw statusEnum={}", statusEnum.name());

        //更新订单结算状态
        UpdateWrapper<TradeWithdrawOrder> withdrawOrderUpdate = new UpdateWrapper<>();
        withdrawOrderUpdate.lambda()
                .set(TradeWithdrawOrder::getSettleStatus, statusEnum.getCode())
                .set(TradeWithdrawOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradeWithdrawOrder::getSettleFinishTime, System.currentTimeMillis())
                .eq(TradeWithdrawOrder::getTradeNo, command.getTradeNo());
        boolean update = tradeWithdrawOrderRepository.update(withdrawOrderUpdate);
        log.info("handlerSettleFinish4Withdraw update order result={}", update);
    }

}
