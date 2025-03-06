package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantCashPaymentChannelConfigDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付名称
     */
    private String paymentName;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 费用
     */
    private BigDecimal singleFee;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 单笔最小
     */
    private BigDecimal amountLimitMin;

    /**
     * 单笔最大
     */
    private BigDecimal amountLimitMax;

    /**
     * 结算配置
     */
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;

}
