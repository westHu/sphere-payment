package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WithdrawFlagReq {

    /**
     * 商户号
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 提现日期
     */
    @NotBlank(message = "withdrawDate is required")
    private String withdrawDate;

}
