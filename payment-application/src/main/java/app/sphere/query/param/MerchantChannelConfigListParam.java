package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantChannelConfigListParam extends MerchantQuerySourceParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 状态
     */
    private Boolean status;

}
