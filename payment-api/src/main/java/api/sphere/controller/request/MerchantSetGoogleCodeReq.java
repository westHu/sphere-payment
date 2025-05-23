package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantSetGoogleCodeReq extends QuerySourceReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * 验证秘钥
     */
    @NotBlank(message = "loginAuth is required")
    private String loginAuth;

    /**
     * 验证码
     */
    @NotBlank(message = "authCode is required")
    private String authCode;

}
