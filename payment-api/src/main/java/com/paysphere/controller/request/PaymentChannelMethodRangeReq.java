package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentChannelMethodRangeReq {

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 交易方向
     */
    @NotNull(message = "paymentDirection is required")
    private Integer paymentDirection;

}
