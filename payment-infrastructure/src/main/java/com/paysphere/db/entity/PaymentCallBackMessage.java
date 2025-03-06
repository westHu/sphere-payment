package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_callback_message")
public class PaymentCallBackMessage extends BaseEntity {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道流水号
     */
    private String channelOrderNo;

    /**
     * 消息体
     */
    private String message;

}
