package api.sphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantInfoReq {

    /**
     * 商户ID
     */
    @NotNull(message = "merchantId is required")
    @Length(max = 16, message = "merchantId max length 16")
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 操作员名称
     */
    private String username;

}
