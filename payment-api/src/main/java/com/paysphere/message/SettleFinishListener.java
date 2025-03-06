package com.paysphere.message;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.cmd.SettleFinishMessageCommand;
import com.paysphere.command.handler.SettleFinish4CashHandler;
import com.paysphere.command.handler.SettleFinish4PayHandler;
import com.paysphere.command.handler.SettleFinish4RechargeHandler;
import com.paysphere.command.handler.SettleFinish4TransferHandler;
import com.paysphere.command.handler.SettleFinish4WithdrawHandler;
import com.paysphere.convert.JobConverter;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.message.dto.SettleFinishMessageDTO;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.utils.PlaceholderUtil;
import com.paysphere.utils.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.paysphere.TradeConstant.SETTLE_FINISH_TOPIC;
import static com.paysphere.TradeConstant.TRADE_SETTLE_FINISH_CONSUMER_GROUP;

/**
 * 接受settle回调. 执行结算
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = SETTLE_FINISH_TOPIC,
        consumerGroup = TRADE_SETTLE_FINISH_CONSUMER_GROUP,
        maxReconsumeTimes = 12,
        delayLevelWhenNextConsume = 9) // 9 -> 每隔5分钟再次消费 重试12次
public class SettleFinishListener implements RocketMQListener<SettleFinishMessageDTO> {

    @Resource
    SettleFinish4PayHandler settleFinish4PayHandler;
    @Resource
    SettleFinish4CashHandler settleFinish4CashHandler;
    @Resource
    SettleFinish4TransferHandler settleFinish4TransferHandler;
    @Resource
    SettleFinish4RechargeHandler settleFinish4RechargeHandler;
    @Resource
    SettleFinish4WithdrawHandler settleFinish4WithdrawHandler;
    @Resource
    JobConverter jobConverter;
    @Resource
    RocketMqProducer rocketMqProducer;


    @Override
    public void onMessage(SettleFinishMessageDTO dto) {
        log.info("settleFinishListener message = {}", JSONUtil.toJsonStr(dto));

        // 校验消息
        String errorMsg = ValidationUtil.getErrorMsg(dto);
        if (StringUtils.isNotBlank(errorMsg)) {
            String msg = PlaceholderUtil.resolve("settleFinishListener message validate false, {1}", errorMsg);
            log.error(msg);
            rocketMqProducer.syncSendExceptionMessage(msg);
            return;
        }

        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(dto.getTradeNo());
        SettleFinishMessageCommand command = jobConverter.convertSettleFinishMessageCommand(dto);
        log.info("settleFinishListener tradeTypeEnum = {}", tradeTypeEnum);

        // 处理结算回调
        try {
            if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
                settleFinish4PayHandler.handlerSettleFinish4Pay(command);
            } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
                settleFinish4CashHandler.handlerSettleFinish4Cash(command);
            } else if (tradeTypeEnum.equals(TradeTypeEnum.TRANSFER)) {
                settleFinish4TransferHandler.handlerSettleFinish4Transfer(command);
            } else if (tradeTypeEnum.equals(TradeTypeEnum.RECHARGE)) {
                settleFinish4RechargeHandler.handlerSettleFinish4Recharge(command);
            } else if (tradeTypeEnum.equals(TradeTypeEnum.WITHDRAW)) {
                settleFinish4WithdrawHandler.handlerSettleFinish4Withdraw(command);
            } else {
                log.warn("settleFinishListener unsupported tradeTypeEnum");
            }
        } catch (Exception e) {
            log.error("settleFinishListener exception", e);
            rocketMqProducer.syncSendExceptionMessage("settle consumer message exception: " + e);
            return;
        }

        log.info("settleFinishListener over. dto id={}", dto.getTradeNo());
    }

}