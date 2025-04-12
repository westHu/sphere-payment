package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnsetGoogleCodeReq extends QuerySourceReq {

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
     * 验证码 不必传，如果是管理平台解绑则不必传
     */
    private String authCode;

}
