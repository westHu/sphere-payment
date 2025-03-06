package com.paysphere.query.param;

import lombok.Data;

@Data
public class SettleGroupListParam {

    /**
     * 交易开始时间
     */
    private String tradeStartTime;

    /**
     * 交易结束时间
     */
    private String tradeEndTime;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

}
