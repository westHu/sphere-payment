package app.sphere.command.handler;

import app.sphere.command.cmd.SettleFinishCommand;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradePaymentOrderRepository;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import share.sphere.enums.SettleStatusEnum;

import javax.annotation.Resource;


@Slf4j
@Component
public class SettleFinish4PaymentHandler {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;

    /**
     * 代收结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Pay(SettleFinishCommand command) {
        log.info("handlerSettleFinish4Pay command={}", JSONUtil.toJsonStr(command));

        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Pay statusEnum={}", statusEnum.name());

        //更新订单结算状态
        UpdateWrapper<TradePaymentOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(TradePaymentOrder::getSettleStatus, statusEnum.getCode())
                .set(TradePaymentOrder::getSettleResult, JSONUtil.toJsonStr(command))
                .set(TradePaymentOrder::getSettleFinishTime, System.currentTimeMillis())
                .eq(TradePaymentOrder::getTradeNo, command.getTradeNo());
        boolean update = tradePaymentOrderRepository.update(updateWrapper);
        log.info("handlerSettleFinish4Pay update order result={}", update);
    }

}
