package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleAmountReq {

    /**
     * 账户(收款)
     */
    @NotBlank(message = "accountNo is required")
    private String accountNo;

    /**
     * 结算日期
     */
    @NotBlank(message = "accountNo is required")
    private String settleStartDate;

    /**
     * 结算日期
     */
    @NotBlank(message = "accountNo is required")
    private String settleEndDate;
}
