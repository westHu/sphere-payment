package api.sphere.controller.response;

import lombok.Data;

@Data
public class MerchantVO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户ID
     */
    private String merchantName;

    /**
     * 商户账户
     */
    private String accountNo;

    /**
     * 店铺名称
     */
    private String shopName;
}
