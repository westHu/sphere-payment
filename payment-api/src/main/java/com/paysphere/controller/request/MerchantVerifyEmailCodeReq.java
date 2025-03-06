package com.paysphere.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantVerifyEmailCodeReq {

    /**
     * 邮箱
     */
    @NotBlank(message = "email is required")
    @Email(message = "email format is illegible")
    @Length(max = 64, message = "email max length 64-character")
    private String email;

    /**
     * 验证码
     */
    @NotBlank(message = "code is required")
    @Length(min = 6, max = 6, message = "code must be 6-digit number")
    private String code;
}
