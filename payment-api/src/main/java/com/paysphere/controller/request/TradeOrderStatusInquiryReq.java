package com.paysphere.controller.request;

import lombok.Data;

@Data
public class TradeOrderStatusInquiryReq {

    /**
     * 订单类型 1 收款 2 代付
     */
    private Integer tradeType;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 交易订单号
     */
    private String tradeNo;

}
