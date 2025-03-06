package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleAccountSnapshotStatementReq extends PageReq {

    /**
     * 日期
     */
    @NotBlank(message = "accountDate is required")
    private String accountDate;

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 状态
     */
    private Boolean status;
}
