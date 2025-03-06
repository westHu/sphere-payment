package com.paysphere.controller.request;

import lombok.Data;

@Data
public class SettleOrderPageReq extends PageReq {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 结算类型
     */
    private String settleType;

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

    /**
     * 结算状态
     */
    private Integer settleStatus;

    /**
     * 交易开始时间
     */
    private String tradeStartTime;

    /**
     * 交易开始时间
     */
    private String tradeEndTime;

}
