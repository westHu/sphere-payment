package com.paysphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户支付配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_pay_config")
public class MerchantPaymentConfig extends BaseEntity {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 收款人工审核开关
     */
    private boolean payReview;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 扩展信息
     */
    private String attribute;

}
