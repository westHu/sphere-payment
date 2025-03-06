package com.paysphere.message;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.TradeCallBackCmdService;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.paysphere.TradeConstant.TRADE_CALLBACK_CONSUMER_GROUP;
import static com.paysphere.TradeConstant.TRADE_CALLBACK_TOPIC;

/**
 * 接受trade回调. 执行订单的商户回调 (自产自消)
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = TRADE_CALLBACK_TOPIC,
        consumerGroup = TRADE_CALLBACK_CONSUMER_GROUP,
        maxReconsumeTimes = 6,
        delayLevelWhenNextConsume = 9) // 9 -> 每隔5分钟再次消费 重试6次
public class TradeCallBackListener implements RocketMQListener<TradeCallBackDTO> {

    @Resource
    TradeCallBackCmdService tradeCallBackCmdService;

    @Override
    public void onMessage(TradeCallBackDTO dto) {
        log.info("tradeCallBackListener message = {}", JSONUtil.toJsonStr(dto));
        boolean callback;
        try {
            callback = tradeCallBackCmdService.handlerTradeCallback(dto);
            log.info("tradeCallBackListener result={}", callback);
        } catch (Exception e) {
            log.error("tradeCallBackListener exception", e);
            callback = true;
        }
        if (!callback) {
            throw new PaymentException(ExceptionCode.MESSAGE_CONSUMER_LATER, "callback");
        }
        log.info("tradeCallBackListener over. dto id={}", dto.getBody().getTradeNo());
    }


}