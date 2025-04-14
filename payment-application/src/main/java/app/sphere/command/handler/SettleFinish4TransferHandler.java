package app.sphere.command.handler;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import app.sphere.command.cmd.SettleFinishCommand;
import infrastructure.sphere.db.entity.TradeTransferOrder;
import share.sphere.enums.SettleStatusEnum;
import domain.sphere.repository.TradeTransferOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Slf4j
@Component
public class SettleFinish4TransferHandler {

    @Resource
    TradeTransferOrderRepository tradeTransferOrderRepository;


    /**
     * 转账结算结果 -> 更新订单结算状态、结算时间、结算结果
     */
    public void handlerSettleFinish4Transfer(SettleFinishCommand command) {
        log.info("handlerSettleFinish4Transfer command={}", JSONUtil.toJsonStr(command));

        SettleStatusEnum statusEnum = command.isSettleStatus() ? SettleStatusEnum.SETTLE_SUCCESS :
                SettleStatusEnum.SETTLE_FAILED;
        log.info("handlerSettleFinish4Transfer statusEnum={}", statusEnum.name());

        //更新订单结算状态
        UpdateWrapper<TradeTransferOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(TradeTransferOrder::getSettleStatus, statusEnum.getCode())
                .set(TradeTransferOrder::getSettleResult, JSONUtil.toJsonPrettyStr(command))
                .set(TradeTransferOrder::getSettleFinishTime, System.currentTimeMillis())
                .eq(TradeTransferOrder::getTradeNo, command.getTradeNo());
        boolean update = tradeTransferOrderRepository.update(updateWrapper);
    }


}
