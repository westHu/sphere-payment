package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户基本配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_config")
public class MerchantConfig extends BaseEntity {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户秘钥
     */
    private String merchantSecret;

    /**
     * 商户收款完成回调地址
     */
    private String finishPaymentUrl;

    /**
     * 商户出款完成回调地址
     */
    private String finishCashUrl;

    /**
     * 商户退款完成回调地址
     */
    private String finishRefundUrl;

    /**
     * 商户支付完成跳转地址
     */
    private String finishRedirectUrl;

    /**
     * 商户公钥
     */
    private String publicKey;

    /**
     * 商户ip白名单
     */
    private String ipWhiteList;

    /**
     * 支付链接设置
     */
    private String paymentLinkSetting;

    /**
     * 凭证设置
     */
    private String receiptSetting;

    /**
     * 扩展信息
     */
    private String attribute;

}
