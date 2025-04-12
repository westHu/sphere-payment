package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SandboxMerchantConfigUpdateCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * '收款完成回调地址'
     */
    private String finishPaymentUrl;

    /**
     * '收款完成回调地址'
     */
    private String finishCashUrl;

    /**
     * '收款完成回调地址'
     */
    private String finishRefundUrl;

    /**
     * '支付完成跳转地址'
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
