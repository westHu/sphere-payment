package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Data
public class SandboxMerchantConfigUpdateReq {

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
     * 商户公钥
     */
    @Length(min = 390, max = 400, message = "publicKey is 2048bit")
    private String publicKey;

    /**
     * 商户ip白名单
     */
    private String ipWhiteList;

}
