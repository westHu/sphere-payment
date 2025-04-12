package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantConfigUpdateReq extends MerchantIdReq {

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
    @Length(min = 458, max = 458, message = "publicKey format is illegal. [2048][PKCS#1]")
    private String publicKey;

    /**
     * 商户ip白名单
     */
    private String ipWhiteList;

}
