package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantOperatorAddReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 操作员姓名
     */
    @NotBlank(message = "merchantId is required")
    @Length(max = 32, message = "username max length 32-character")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "password is required")
    @Length(min = 8, max = 16, message = "password must be 8 to 16 character")
    private String password;

    /**
     * 头像
     */
    private String icon;

    /**
     * 描述
     */
    @Length(max = 128, message = "desc max length 128-character")
    private String desc;
}
