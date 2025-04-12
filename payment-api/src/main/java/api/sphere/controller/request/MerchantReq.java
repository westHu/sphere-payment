package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantReq {

    /**
     * 商户ID 必传
     */
    @NotBlank(message = "merchantId is required")
    @Length(max = 32, message = "merchantId max 32")
    private String merchantId;

    /**
     * 商户名称
     */
    @Length(max = 64, message = "merchantName max 64")
    private String merchantName;

}
