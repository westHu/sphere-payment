package com.paysphere.controller.response;

import lombok.Data;

/**
 * 商户收款支付配置
 */
@Data
public class MerchantPayConfigVO {

    /**
     * 商户ID
     */
    private String merchantId;


    /**
     * 收款人工审核开关
     */
    private Boolean payReview;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 扩展信息
     */
    private String attribute;


}
