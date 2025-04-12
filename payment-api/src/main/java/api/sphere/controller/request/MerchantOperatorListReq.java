package api.sphere.controller.request;

import lombok.Data;

@Data
public class MerchantOperatorListReq {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 操作员姓名
     */
    private String username;
}
