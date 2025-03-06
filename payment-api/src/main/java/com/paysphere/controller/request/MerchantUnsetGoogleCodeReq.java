package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantUnsetGoogleCodeReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * 验证码 不必传，如果是管理平台解绑则不必传
     */
    private String authCode;

    /**
     * 商户查询来源
     */
    @NotNull(message = "querySource is required")
    private Integer querySource;
}
