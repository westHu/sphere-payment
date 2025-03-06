package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleAccountBalanceApiQueryReq {

    /**
     * 账户号
     */
    @NotBlank(message = "accountNo is required")
    private String accountNo;

    /**
     * 扩展
     */
    private String additionalInfo;
}
