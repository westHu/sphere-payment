package com.paysphere.command.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AccountApplyDanaDTO {

    /**
     * 姓名
     */
    @NotBlank(message = "name is required")
    @Length(max = 64, message = "username max length 64-character")
    private String name;

    /**
     * 电话
     */
    @NotBlank(message = "phone is required")
    @Length(min = 10, max = 20, message = "phone must be 10 to 20 character")
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "email is required")
    @Email
    private String email;

    /**
     * 地址
     */
    @NotNull(message = "address is required")
    @Valid
    private AccountApplyAddressDTO address;

}
