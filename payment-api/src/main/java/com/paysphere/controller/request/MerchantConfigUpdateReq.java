package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
public class MerchantConfigUpdateReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 收款完成回调地址
     */
    @URL(message = "finishPaymentUrl is URL")
    private String finishPaymentUrl;

    /**
     * 收款完成回调地址
     */
    @URL(message = "finishCashUrl must be URL")
    private String finishCashUrl;

    /**
     * 收款完成回调地址
     */
    @URL(message = "finishRefundUrl must be URL")
    private String finishRefundUrl;

    /**
     * 支付完成跳转地址
     */
    @URL(message = "finishRedirectUrl must be URL")
    private String finishRedirectUrl;

    /**
     * 商户公钥 2048 PEM格式的必须是392位字符串
     */
    @Length(min = 392, max = 392, message = "publicKey format is illegal. RSA Node: 2048")
    private String publicKey;

    /**
     * 商户ip白名单
     */
    private String ipWhiteList;

}
