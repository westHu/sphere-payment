package com.paysphere.message;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.cmd.PaymentFinishMessageCommand;
import com.paysphere.command.handler.PaymentFinish4CashHandler;
import com.paysphere.command.handler.PaymentFinish4PayHandler;
import com.paysphere.convert.JobConverter;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.message.dto.PaymentFinishMessageDTO;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.utils.PlaceholderUtil;
import com.paysphere.utils.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.paysphere.TradeConstant.PAYMENT_FINISH_TOPIC;
import static com.paysphere.TradeConstant.TRADE_PAYMENT_FINISH_CONSUMER_GROUP;

/**
 * 接受payment回调. 执行商户回调->结算
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = PAYMENT_FINISH_TOPIC,
        consumerGroup = TRADE_PAYMENT_FINISH_CONSUMER_GROUP,
        maxReconsumeTimes = 12,
        delayLevelWhenNextConsume = 9) // 9 -> 每隔5分钟再次消费 重试12次
public class PaymentFinishListener implements RocketMQListener<PaymentFinishMessageDTO> {

    @Resource
    PaymentFinish4PayHandler paymentFinish4PayHandler;
    @Resource
    PaymentFinish4CashHandler paymentFinish4CashHandler;
    @Resource
    JobConverter jobConverter;
    @Resource
    RocketMqProducer rocketMqProducer;


    @Override
    public void onMessage(PaymentFinishMessageDTO dto) {
        log.info("paymentFinishListener message = {}", JSONUtil.toJsonStr(dto));

        // 校验消息
        String errorMsg = ValidationUtil.getErrorMsg(dto);
        if (StringUtils.isNotBlank(errorMsg)) {
            String msg = PlaceholderUtil.resolve("paymentFinishListener message validate false, {1}", errorMsg);
            log.error(msg);
            rocketMqProducer.syncSendExceptionMessage(msg);
            return;
        }

        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(dto.getTradeNo());
        PaymentFinishMessageCommand command = jobConverter.convertPaymentFinishMessageCommand(dto);
        log.info("paymentFinishListener tradeTypeEnum={}", tradeTypeEnum);

        // 处理支付回调
        try {
            if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
                paymentFinish4PayHandler.handlerPaymentFinish4Pay(command);

            } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
                paymentFinish4CashHandler.handlerPayment4FinishCash(command);
            } else {
                log.warn("paymentFinishListener unsupported tradeType");
            }
        } catch (Exception e) {
            log.error("paymentFinishListener exception", e);

            // 如果指定异常需要再次消费，则抛出异常等待再次消费
            String message = e.getMessage();
            if (message.equals(ExceptionCode.MESSAGE_CONSUMER_LATER.getMessage())) {
                throw new PaymentException(ExceptionCode.MESSAGE_CONSUMER_LATER);
            }

            rocketMqProducer.syncSendExceptionMessage("paymentFinishListener exception: " + message);
            return;
        }

        log.info("paymentFinishListener opt over. dto id={}", dto.getTradeNo());
    }


}