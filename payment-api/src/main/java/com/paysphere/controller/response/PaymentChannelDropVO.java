package com.paysphere.controller.response;

import lombok.Data;

@Data
public class PaymentChannelDropVO {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 对接的渠道名称
     */
    private String channelName;

}
