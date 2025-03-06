package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码重置
 */
@Data
public class MerchantPasswordResetReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    private String username;

}
