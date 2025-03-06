package com.paysphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentChannelUpdateReq {

    /**
     * ID
     */
    @NotNull(message = "id is null")
    private Long id;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * API地址
     */
    private String url;

    /**
     * 授权
     */
    private String license;

    /**
     * 是否需要进件
     */
    private Boolean division;
}
