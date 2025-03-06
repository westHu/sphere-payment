package com.paysphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对接的平台/银行
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_channel")
public class PaymentChannel extends BaseEntity {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道类型 银行？三方？ 四方？ 杂牌？
     */
    private String channelType;

    /**
     * 返回模式：0:收银台、1:收款号、4:都有
     */
    private Integer returnMode;

    /**
     * some url for api
     */
    private String url;

    /**
     * code\key
     */
    private String license;

    /**
     * 状态
     */
    private boolean status;

    /**
     * note something
     */
    private String attribute;

}