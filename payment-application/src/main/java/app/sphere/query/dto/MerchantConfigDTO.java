package app.sphere.query.dto;

import lombok.Data;

/**
 * 商户基本配置
 */
@Data
public class MerchantConfigDTO {

    /**
     * 商户ID
     */
    private String merchantId;

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
    private String finishPayoutUrl;

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


}
