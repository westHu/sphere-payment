package com.paysphere.controller.request;

import lombok.Data;

@Data
public class TradePaymentRefundReq extends TradeNoReq {

    /**
     * 操作员
     */
    private String operator;

}
