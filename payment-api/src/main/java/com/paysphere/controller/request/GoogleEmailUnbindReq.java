package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleEmailUnbindReq {

    /**
     * 登录名
     */
    @NotBlank(message = "username is required")
    private String username;

}
