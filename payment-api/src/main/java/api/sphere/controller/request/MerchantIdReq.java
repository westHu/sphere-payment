package api.sphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantIdReq extends QuerySourceReq {

    /**
     * 商户ID
     */
    @NotNull(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

}
