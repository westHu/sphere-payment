package app.sphere.query.param;

import lombok.Data;

@Data
public class MerchantCashPaymentConfigParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 状态
     */
    private Boolean status;
}
