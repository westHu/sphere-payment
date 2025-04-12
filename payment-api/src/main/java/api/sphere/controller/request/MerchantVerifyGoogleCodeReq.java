package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantVerifyGoogleCodeReq extends QuerySourceReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    @Length(max = 64, message = "username max length 64-character")
    private String username;

    /**
     * 验证码
     */
    @NotBlank(message = "authCode is required")
    @Length(min = 6, max = 6, message = "authCode must be 6-digit number")
    private String authCode;
}
