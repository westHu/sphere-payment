package com.paysphere.controller.request;

import lombok.Data;

@Data
public class PaymentChannelReq {

    /**
     * 对接的渠道编码
     * unique no
     */
    private String channelCode;

    /**
     * 对接的渠道名称：duiTku、MidTrans、DANA...
     * unique name
     */
    private String channelName;

    /**
     * 0/1  Valid Invalid
     */
    private Boolean status;

    /**
     * 是否需要进件，创建子商户
     */
    private Boolean division;
}
