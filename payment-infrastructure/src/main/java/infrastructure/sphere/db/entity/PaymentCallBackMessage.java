package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付回调消息实体
 * 记录支付渠道的回调消息，用于异步通知和消息追踪
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_callback_message")
public class PaymentCallBackMessage extends BaseEntity {

    // ============== 渠道信息 ==============
    /**
     * 渠道编码
     * 支付渠道的唯一标识符，如：ALIPAY、WECHAT等
     */
    private String channelCode;

    /**
     * 渠道名称
     * 支付渠道的显示名称，如：支付宝、微信支付等
     */
    private String channelName;

    // ============== 交易信息 ==============
    /**
     * 交易流水号
     * 系统内部的交易流水号
     */
    private String tradeNo;

    /**
     * 渠道流水号
     * 支付渠道返回的交易流水号
     */
    private String channelOrderNo;

    // ============== 消息内容 ==============
    /**
     * 消息体
     * 支付渠道回调的原始消息内容，JSON格式
     */
    private String message;
}
