package app.sphere.query.param;

import lombok.Data;

@Data
public class MerchantQuerySourceParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 来源
     */
    private Integer querySource;
}
