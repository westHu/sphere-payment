package api.sphere.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantVerifyReq extends OperatorReq {

    /**
     * 商户ID
     */
    private String merchantId;

}
