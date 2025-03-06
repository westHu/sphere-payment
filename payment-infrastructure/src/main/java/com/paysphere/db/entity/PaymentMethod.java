package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 支付方式  可能是银行、可能是三方
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_method")
public class PaymentMethod extends BaseEntity {

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
     * 支付方式类型：信用卡、虚拟号、二维码等
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

    /**
     * 备注
     */
    private String attribute;

}
