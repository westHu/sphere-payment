package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeLarkInquiryDecryptReq {

    /**
     * 加密字符串
     */
    @NotBlank(message = "encrypt is required")
    private String encrypt;

}
