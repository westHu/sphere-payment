package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleAccountAmountUnfrozenReq {

    /**
     * 交易单号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 外部订单号
     */
    private String outerNo;

}
