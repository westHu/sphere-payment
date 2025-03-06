package com.paysphere.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询支付方式
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentMethodPageReq extends PageReq {

    /**
     * 支付类型
     */
    private Integer paymentType;

    /**
     * 支付方向
     */
    private Integer paymentDirection;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 状态
     */
    private Boolean status;

}
