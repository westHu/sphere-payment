package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录信息
 */
@Data
public class MerchantLoginReq {

    /**
     * 登录类型
     */
    @NotBlank(message = "mode is required")
    private String mode;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
