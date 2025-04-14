package app.sphere.command.cmd;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantConfigUpdateCmd extends MerchantIdCommand {

    /**
     * '收款完成回调地址'
     */
    private String finishPaymentUrl;

    /**
     * '收款完成回调地址'
     */
    private String finishPayoutUrl;

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
