package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentLinkSettingReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 配置参数
     */
    @NotBlank(message = "paymentLinkSetting is required")
    private String paymentLinkSetting;

}
