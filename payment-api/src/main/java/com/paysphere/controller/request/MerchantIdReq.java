package com.paysphere.controller.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantIdReq extends MerchantQuerySourceReq {

    /**
     * 商户ID
     */
    @NotNull(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

}
