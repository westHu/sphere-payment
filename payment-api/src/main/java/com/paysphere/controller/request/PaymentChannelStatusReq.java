package com.paysphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentChannelStatusReq {

    /**
     * ID
     */
    @NotNull(message = "Id is null")
    private Long id;

    /**
     * 状态
     */
    @NotNull(message = "status is null")
    private Boolean status;

    /**
     * 是否一起更新channelMethod
     */
    private Boolean relationMethod = true;


    /**
     * url
     */
    private String url;

    /**
     * license
     */
    private String license;
}
