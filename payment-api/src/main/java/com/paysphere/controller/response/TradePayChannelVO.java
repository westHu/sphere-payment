package com.paysphere.controller.response;

import lombok.Data;

@Data
public class TradePayChannelVO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 虚拟号VA
     */
    private String vaNumber;

    /**
     * QR
     */
    private String qrString;

    /**
     * 支付链接
     */
    private String paymentUrl;

    /**
     * additional information
     */
    private ChannelAdditionalInfoVO additionalInfo;
}
