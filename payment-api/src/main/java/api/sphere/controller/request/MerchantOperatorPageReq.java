package api.sphere.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantOperatorPageReq extends PageReq {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 操作员姓名
     */
    private String username;

}
