package com.paysphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户支付配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_cash_config")
public class MerchantPayoutConfig extends BaseEntity {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 代付人工审核开关
     */
    private boolean cashReview;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 扩展信息
     */
    private String attribute;

}
