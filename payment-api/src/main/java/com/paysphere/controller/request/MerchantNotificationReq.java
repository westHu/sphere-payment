package com.paysphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantNotificationReq {

    /**
     * 商户ID
     */
    @NotNull(message = "merchantId is required")
    private String merchantId;

    /**
     * 标题
     */
    private String title;

    /**
     * 是否已阅读
     */
    private Boolean read;

    /**
     * 是否有效
     */
    private Boolean status;

}
