package com.paysphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentPayPaymentConfigVO {

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 代理商名称
     */
    private String agentName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付名称
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
     * 状态
     */
    private boolean status;

}
