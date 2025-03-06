package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantUsingPaymentParam {

    /**
     * 交易方向 见枚举
     */
    private Integer paymentDirection;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 交易类型
     */
    private Integer tradeType;

}
