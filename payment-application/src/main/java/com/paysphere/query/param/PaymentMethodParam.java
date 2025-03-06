package com.paysphere.query.param;

import lombok.Data;

@Data
public class PaymentMethodParam {

    /**
     * 支付方向， 只能查询单一方向
     */
    private Integer paymentDirection;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 状态
     */
    private Boolean status;

}
