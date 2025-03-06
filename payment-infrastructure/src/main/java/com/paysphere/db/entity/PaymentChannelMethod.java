package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author dh
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_channel_method")
public class PaymentChannelMethod extends BaseEntity {

    /**
     * channel
     * 对接的渠道编码
     */
    private String channelCode;

    /**
     * channel
     * 对接的渠道名称
     */
    private String channelName;

    /**
     * payment
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 交易方向 见枚举
     */
    private Integer paymentDirection;

    /**
     * attribute
     * 属性
     */
    private String paymentAttribute;

    /**
     * 描述
     */
    private String description;

    /**
     * 结算周期
     */
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;

    /**
     * 单笔费用
     */
    private BigDecimal singleFee;

    /**
     * 单笔费率
     */
    private BigDecimal singleRate;

    /**
     * 单笔金额下限
     */
    private BigDecimal amountLimitMin;

    /**
     * 单笔金额上限
     */
    private BigDecimal amountLimitMax;

    /**
     * Lower times limit
     * 次数下限
     */
    private Integer timesLimitMin = 0;

    /**
     * Limit times ceiling
     * 次数上限
     */
    private Integer timesLimitMax = 10000;

    /**
     * 成功率
     */
    private BigDecimal successRate = new BigDecimal(1);

    /**
     * 0/1 status
     * 状态
     */
    private boolean status;

    /**
     * attribute
     */
    private String attribute;

}
