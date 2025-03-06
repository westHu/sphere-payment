package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户代付支付配置
 */
@Data
public class MerchantPayoutConfigDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 代付人工审核开关
     */
    private Boolean cashReview;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 最大金额值
     */
    private BigDecimal maximumAmount;

    /**
     * 最大限次数
     */
    private Integer maximumTimes;

    /**
     * 扩展信息
     */
    private String attribute;

}
