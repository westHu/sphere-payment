package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantChannelConfigUpdateByPaymentReq {

    /**
     * 支付方式
     */
    @NotBlank(message = "channelCode is required")
    private String paymentMethod;

    /**
     * 商户渠道配置状态
     */
    @NotNull(message = "status is required")
    private Boolean status;

}
