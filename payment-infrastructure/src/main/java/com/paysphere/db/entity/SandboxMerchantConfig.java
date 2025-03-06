package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户沙箱基本配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sandbox_merchant_config")
public class SandboxMerchantConfig extends BaseEntity {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户号
     */
    private String merchantCode;

    /**
     * 商户名称
     */
    private String merchantName;

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
     * 版本
     */
    private Integer version;

    /**
     * 扩展信息
     */
    private String attribute;

}
