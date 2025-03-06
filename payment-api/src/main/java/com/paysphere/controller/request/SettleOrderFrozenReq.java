package com.paysphere.controller.request;

import lombok.Data;

import java.util.List;

@Data
public class SettleOrderFrozenReq extends OperatorReq {

    /**
     * ID
     */
    private List<Long> idList;

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
     * 交易开始时间
     */
    private String tradeStartTime;

    /**
     * 交易开始时间
     */
    private String tradeEndTime;

}
