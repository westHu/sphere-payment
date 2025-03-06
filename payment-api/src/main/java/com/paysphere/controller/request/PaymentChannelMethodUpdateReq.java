package com.paysphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentChannelMethodUpdateReq {

    /**
     * id
     */
    @NotNull(message = "id is null")
    private Long id;

    /**
     * 支付方式属性
     */
    private String paymentAttribute;

    /**
     * 单笔手续费
     */
    private BigDecimal singleFee;

    /**
     * 单笔手续费率
     */
    private BigDecimal singleRate;

    /**
     * 限额下限
     */
    private BigDecimal amountLimitMin;

    /**
     * 限额上限
     */
    private BigDecimal amountLimitMax;

}
