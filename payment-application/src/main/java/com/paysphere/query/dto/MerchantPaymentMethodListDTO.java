package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantPaymentMethodListDTO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 费用
     */
    private BigDecimal singleFee;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 支付最小限额
     */
    private BigDecimal amountLimitMin;

    /**
     * 支付最大限额
     */
    private BigDecimal amountLimitMax;

    /**
     * 结算方式
     */
    private String settleType;

}
