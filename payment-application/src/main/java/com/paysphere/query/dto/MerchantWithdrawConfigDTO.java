package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户代付支付配置
 */
@Data
public class MerchantWithdrawConfigDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 提现人工审核开关
     */
    private Boolean withdrawReview;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 提现费用
     */
    private BigDecimal withdrawFee;

    /**
     * 提现费率
     */
    private BigDecimal withdrawRate;


}
