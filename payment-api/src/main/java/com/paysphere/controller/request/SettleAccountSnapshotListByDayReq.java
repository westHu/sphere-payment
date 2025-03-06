package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleAccountSnapshotListByDayReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 时间
     */
    @NotBlank(message = "accountDate is required")
    private String accountDate;

}
