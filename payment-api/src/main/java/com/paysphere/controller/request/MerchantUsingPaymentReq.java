package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantUsingPaymentReq {

    /**
     * 交易方向 见枚举
     */
    @NotNull(message = "paymentDirection is required")
    private Integer paymentDirection;

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 对接的渠道编码
     * unique no
     */
    @NotBlank(message = "paymentMethod is required")
    private String channelCode;

    /**
     * 对接的渠道名称：duiTku、MidTrans、DANA...
     * unique name
     */
    private String channelName;

}
