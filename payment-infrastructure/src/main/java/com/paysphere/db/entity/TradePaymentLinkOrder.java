package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 支付链接
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_payment_link_order")
public class TradePaymentLinkOrder extends BaseEntity {

    /**
     * 支付链接单号
     */
    private String linkNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private Integer linkStatus;

    /**
     * 备注
     */
    private String notes;

    /**
     * 支付链接
     */
    private String paymentLink;

    /**
     * version
     */
    private Integer version;

    /**
     * version
     */
    private Integer area;

    /**
     * 扩展信息
     */
    private String attribute;

}
