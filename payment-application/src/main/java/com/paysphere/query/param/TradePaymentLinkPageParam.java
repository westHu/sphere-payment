package com.paysphere.query.param;

import lombok.Data;

@Data
public class TradePaymentLinkPageParam extends PageParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 支付链接单号
     */
    private String linkNo;

    /**
     * 支付链接
     */
    private String paymentLink;

    /**
     * 创建开始时间
     */
    private String createStartTime;

    /**
     * 创建结束时间
     */
    private String createEndTime;

}
