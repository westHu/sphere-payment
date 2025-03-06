package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeWithdrawOrderPageReq extends PageReq {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 交易开始时间
     */
    @NotBlank(message = "tradeStartTime is required")
    private String tradeStartTime;

    /**
     * 交易结束时间
     */
    @NotBlank(message = "tradeEndTime is required")
    private String tradeEndTime;

}
