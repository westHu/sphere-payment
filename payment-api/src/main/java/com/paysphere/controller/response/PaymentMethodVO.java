package com.paysphere.controller.response;

import lombok.Data;

@Data
public class PaymentMethodVO {

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 支付方式作用， 用于收款、付款等
     * 二进制状态法
     */
    private Integer paymentDirection;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 支付方式简称
     */
    private String paymentAbbr;

    /**
     * 支付方式类型：信用卡、VA、QR等
     */
    private Integer paymentType;

    /**
     * 支付方式图标
     */
    private String paymentIcon;

    /**
     * 状态
     */
    private boolean status;

    /**
     * 区域
     */
    private Integer area;

}
