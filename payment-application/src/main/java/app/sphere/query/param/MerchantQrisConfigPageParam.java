package app.sphere.query.param;

import lombok.Data;

@Data
public class MerchantQrisConfigPageParam extends PageParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 类型
     */
    private String qrisType;

}
