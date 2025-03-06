package com.paysphere.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * receiver info
 */
@Data
public class ReceiverReq {

    /**
     * 姓名 必填
     */
    @NotBlank(message = "receiver name is required")
    @Length(max = 128, message = "receiver name length max 128")
    private String name;

    /**
     * 电话 必填
     */
    @NotBlank(message = "receiver phone is required")
    @Length(max = 64, message = "receiver phone length max 64")
    private String phone;

    /**
     * 邮箱
     */
    @Length(max = 128, message = "receiver email length max 128")
    @Email(message = "receiver email format error")
    private String email;

    /**
     * 地址
     */
    @Length(max = 256, message = "receiver address length max 256")
    private String address;

    /**
     * 身份ID
     */
    @Length(max = 32, message = "receiver identity length max 32")
    private String identity;

}
