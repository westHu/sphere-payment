package com.paysphere.mq;

import cn.hutool.json.JSONUtil;
import com.paysphere.TradeConstant;
import com.paysphere.mq.dto.email.EmailContentTypeEnum;
import com.paysphere.mq.dto.email.EmailMqMessageContentDTO;
import com.paysphere.mq.dto.email.EmailMqMessageDTO;
import com.paysphere.mq.dto.email.EmailTemplateEnum;
import com.paysphere.mq.dto.email.EmailTradePayinReceiptDTO;
import com.paysphere.mq.dto.email.EmailTradeSendCodeDTO;
import com.paysphere.mq.dto.email.EmailTradeTransferDTO;
import com.paysphere.mq.dto.email.EmailTradeWithdrawDTO;
import com.paysphere.mq.dto.lark.LarkMessageTypeEnum;
import com.paysphere.mq.dto.lark.LarkMqMessageDTO;
import com.paysphere.mq.dto.tg.TgGroupEnum;
import com.paysphere.mq.dto.tg.TgJobMonitorDTO;
import com.paysphere.mq.dto.tg.TgMessageTypeEnum;
import com.paysphere.mq.dto.tg.TgMqMessageDTO;
import com.paysphere.mq.dto.tg.TgTemplateEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * https:// blog.csdn.net/qq_26154077/article/details/111013842
 * https:// blog.csdn.net/weixin_42029738/article/details/119010985
 * <p>
 * 加上配置
 * rocketmq:
 * name-server: 127.0.0.1:9876
 * producer:
 * group: my-producer-group
 */
@Slf4j
@Component
public class RocketMqProducer {

    @Resource
    RocketMQTemplate rocketMQTemplate;
    @Value("${spring.application.name}")
    String applicationName;

    /**
     * 异步发送
     */
    public SendResult syncSend(String topic, String message) {
        log.info("rocketMqProducer send: \n topic={}, \n message={}", topic, message);
        return rocketMQTemplate.syncSend(topic, message);
    }


    /**
     * 通过mq发送异常消息
     */
    public void syncSendExceptionMessage(String message) {
        log.info("syncSendExceptionMessage: message={}", message);
        TgMqMessageDTO tgMessageDTO = new TgMqMessageDTO();
        tgMessageDTO.setApplicationName(applicationName);
        tgMessageDTO.setMessageType(TgMessageTypeEnum.TEXT.name());
        tgMessageDTO.setGroup(TgGroupEnum.GROUP_EXCEPTION.name());
        tgMessageDTO.setHeader(applicationName + " 异常报警:");
        tgMessageDTO.setMessage(message);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(TradeConstant.TG_SEND_MESSAGE_TOPIC,
                    JSONUtil.toJsonStr(tgMessageDTO));
            log.info("syncSendExceptionMessage: sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("syncSendExceptionMessage exception", e);
        }
    }

    /**
     * 通过mq发送job执行消息
     */
    public void syncSendJobMessage(String jobName, String status, String message) {
        log.info("syncSendJobMessage: message={}", message);
        TgJobMonitorDTO jobMonitorDTO = new TgJobMonitorDTO();
        jobMonitorDTO.setJobName(jobName);
        jobMonitorDTO.setJobStatus(status);
        jobMonitorDTO.setJobDesc(message);

        TgMqMessageDTO tgMessageDTO = new TgMqMessageDTO();
        tgMessageDTO.setApplicationName(applicationName);
        tgMessageDTO.setMessageType(TgMessageTypeEnum.TEMPLATE.name());
        tgMessageDTO.setGroup(TgGroupEnum.GROUP_ALARM.name());
        tgMessageDTO.setHeader(applicationName + " 定时任务播报:");

        tgMessageDTO.setTemplateName(TgTemplateEnum.JOB_ALARM.name());
        tgMessageDTO.setTemplateParam(JSONUtil.toJsonStr(jobMonitorDTO));
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(TradeConstant.TG_SEND_MESSAGE_TOPIC,
                    JSONUtil.toJsonStr(tgMessageDTO));
            log.info("syncSendJobMessage: sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("syncSendJobMessage exception", e);
        }
    }

    /**
     * 查单机器人
     */
    public void syncSendInquiryOrderMessage(String message) {
        log.info("syncSendInquiryOrderMessage: message={}", message);
        LarkMqMessageDTO larkMessageDTO = new LarkMqMessageDTO();
        larkMessageDTO.setApplicationName(applicationName);
        larkMessageDTO.setMessageType(LarkMessageTypeEnum.APP_INQUIRY.name());
        larkMessageDTO.setHeader(applicationName + " 查单机器人:");
        larkMessageDTO.setMessage(message);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(TradeConstant.LARK_SEND_MESSAGE_TOPIC,
                    JSONUtil.toJsonStr(larkMessageDTO));
            log.info("syncSendInquiryOrderMessage: sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("syncSendInquiryOrderMessage exception", e);
        }
    }


    /**
     * 发送转账成功邮件
     */
    public void syncSendTradeTransferEmail(String sendTo, String subject, EmailTradeTransferDTO transferDTO) {
        log.info("syncSendTradeTransferEmail: sendTo={}, subject={}, transferDTO={}",
                sendTo, subject, JSONUtil.toJsonStr(transferDTO));
        EmailMqMessageDTO messageDTO = new EmailMqMessageDTO();
        messageDTO.setSendTo(sendTo);
        messageDTO.setSubject(subject);

        EmailMqMessageContentDTO contentDTO = new EmailMqMessageContentDTO();
        contentDTO.setContentType(EmailContentTypeEnum.TEMPLATE.name());
        contentDTO.setTemplateName(EmailTemplateEnum.TRADE_TRANSFER.name());
        contentDTO.setTemplateParam(JSONUtil.toJsonStr(transferDTO));
        messageDTO.setContent(contentDTO);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(TradeConstant.EMAIL_MESSAGE_TOPIC,
                    JSONUtil.toJsonStr(messageDTO));
            log.info("syncSendTradeTransferEmail: sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("syncSendTradeTransferEmail exception", e);
        }
    }


    /**
     * 发送转账成功邮件
     */
    public void syncSendTradeWithdrawEmail(String sendTo, String subject, EmailTradeWithdrawDTO withdrawDTO) {
        log.info("syncSendTradeWithdrawEmail: sendTo={}, subject={}, withdrawDTO={}",
                sendTo, subject, JSONUtil.toJsonStr(withdrawDTO));
        EmailMqMessageDTO messageDTO = new EmailMqMessageDTO();
        messageDTO.setSendTo(sendTo);
        messageDTO.setSubject(subject);

        EmailMqMessageContentDTO contentDTO = new EmailMqMessageContentDTO();
        contentDTO.setContentType(EmailContentTypeEnum.TEMPLATE.name());
        contentDTO.setTemplateName(EmailTemplateEnum.TRADE_WITHDRAW.name());
        contentDTO.setTemplateParam(JSONUtil.toJsonStr(withdrawDTO));
        messageDTO.setContent(contentDTO);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(TradeConstant.EMAIL_MESSAGE_TOPIC,
                    JSONUtil.toJsonStr(messageDTO));
            log.info("syncSendTradeWithdrawEmail: sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("syncSendTradeWithdrawEmail exception", e);
        }
    }

    /**
     * 发送交易验证码
     */
    public void syncSendTradeSendCodeEmail(String sendTo, String subject, String code) {
        log.info("syncSendTradeSendCodeEmail: sendTo={}, subject={}, code={}", sendTo, subject, code);
        EmailMqMessageDTO messageDTO = new EmailMqMessageDTO();
        messageDTO.setSendTo(sendTo);
        messageDTO.setSubject(subject);

        EmailTradeSendCodeDTO sendCodeDTO = new EmailTradeSendCodeDTO();
        sendCodeDTO.setCode(code);

        EmailMqMessageContentDTO contentDTO = new EmailMqMessageContentDTO();
        contentDTO.setContentType(EmailContentTypeEnum.TEMPLATE.name());
        contentDTO.setTemplateName(EmailTemplateEnum.MERCHANT_SEND_CODE.name());
        contentDTO.setTemplateParam(JSONUtil.toJsonStr(sendCodeDTO));
        messageDTO.setContent(contentDTO);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(TradeConstant.EMAIL_MESSAGE_TOPIC,
                    JSONUtil.toJsonStr(messageDTO));
            log.info("syncSendTradeSendCodeEmail: sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("syncSendTradeSendCodeEmail exception", e);
        }
    }

    /**
     * 发送交易收款凭证
     */
    public void syncSendTradePayinReceiptEmail(String sendTo, String subject, EmailTradePayinReceiptDTO receiptDTO) {
        log.info("syncSendTradePayinReceiptEmail: sendTo={}, subject={}, receiptDTO={}", sendTo, subject, receiptDTO);
        EmailMqMessageDTO messageDTO = new EmailMqMessageDTO();
        messageDTO.setSendTo(sendTo);
        messageDTO.setSubject(subject);

        EmailMqMessageContentDTO contentDTO = new EmailMqMessageContentDTO();
        contentDTO.setContentType(EmailContentTypeEnum.TEMPLATE.name());
        contentDTO.setTemplateName(EmailTemplateEnum.TRADE_PAYIN_RECEIPT.name());
        contentDTO.setTemplateParam(JSONUtil.toJsonStr(receiptDTO));
        messageDTO.setContent(contentDTO);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(TradeConstant.EMAIL_MESSAGE_TOPIC,
                    JSONUtil.toJsonStr(messageDTO));
            log.info("syncSendTradePayinReceiptEmail: sendResult={}", sendResult);
        } catch (Exception e) {
            log.error("syncSendTradePayinReceiptEmail exception", e);
        }
    }
}
