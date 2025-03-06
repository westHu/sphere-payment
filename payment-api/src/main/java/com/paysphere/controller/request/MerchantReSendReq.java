package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantReSendReq {

    @NotBlank(message = "username is required")
    private String username;

}
