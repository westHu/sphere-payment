package com.paysphere.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * payer info
 */
@Data
public class PayerReq {

    /**
     * 姓名
     */
    @NotNull(message = "payer name is required")
    @Length(max = 128, message = "payer name length max 128")
    private String name;

    /**
     * 邮箱
     */
    @Length(max = 128, message = "payer email length max 128")
    @Email(message = "payer email format error")
    private String email;

    /**
     * 电话
     */
    @NotNull(message = "payer phone is required")
    @Length(max = 64, message = "payer phone length max 64")
    private String phone;

    /**
     * 地址
     */
    @Length(max = 256, message = "payer address length max 256")
    private String address;

    /**
     * 身份ID
     */
    @Length(max = 32, message = "payer identity length max 32")
    private String identity;
}
