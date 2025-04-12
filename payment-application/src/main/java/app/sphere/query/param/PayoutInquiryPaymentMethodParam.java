package app.sphere.query.param;

import lombok.Data;

@Data
public class PayoutInquiryPaymentMethodParam {

    /**
     * 商户ID
     */
    private MerchantIdParam merchant;

    /**
     * 附件信息
     */
    private String additionalInfo;

}
