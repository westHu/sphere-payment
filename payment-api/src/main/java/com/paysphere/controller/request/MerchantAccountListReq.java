package com.paysphere.controller.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantAccountListReq {

    /**
     * 商户ID
     */
    @NotNull(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户类型
     */
    private Integer accountType;

}
