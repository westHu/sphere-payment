package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantOperatorPageParam extends PageParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 操作员姓名
     */
    private String username;

}
