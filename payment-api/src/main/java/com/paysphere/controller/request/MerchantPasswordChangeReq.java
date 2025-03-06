package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 密码重置
 */
@Data
public class MerchantPasswordChangeReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    @Length(max = 64, message = "username max length 64-character")
    private String username;


    /**
     * 密码
     */
    @NotBlank(message = "oldPassword is required")
    @Length(max = 16, message = "oldPassword max length 16-character")
    private String oldPassword;

    /**
     * 密码
     */
    @NotBlank(message = "newPassword is required")
    @Length(max = 16, message = "newPassword max length 16-character")
    private String newPassword;


}
