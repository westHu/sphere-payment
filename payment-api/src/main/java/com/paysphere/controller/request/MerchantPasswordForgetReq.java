package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 密码重置
 */
@Data
public class MerchantPasswordForgetReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    @Length(max = 64, message = "username max length 64-character")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "password is required")
    @Length(max = 16, message = "password max length 32-character")
    private String password;


}
