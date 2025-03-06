package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "template_merchant_cash_payment_config")
public class MerchantTemplatePayoutChannelConfig extends BaseEntity {

    /**
     * 模版名 支付方式
     */
    private String templateName;

    /**
     * 行业ID
     */
    private Long industryId;

    /**
     * 行业名称
     */
    private String industryName;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

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
     * 最少商户手续费
     */
    private BigDecimal minFee;

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

    /**
     * 地区
     */
    private Integer area;

    /**
     * 状态
     */
    private boolean status;

}
