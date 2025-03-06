package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashierPaymentMethodDTO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付方式简称
     */
    private String paymentAbbr;

    /**
     * 支付方式类型：信用卡、VA、QR等
     */
    private Integer paymentType;

    /**
     * 排序
     */
    private int sort;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 费用
     */
    private BigDecimal singleFee;

}
