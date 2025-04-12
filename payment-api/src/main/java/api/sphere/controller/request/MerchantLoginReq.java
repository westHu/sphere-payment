package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantLoginReq extends QuerySourceReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "password is required")
    private String password;

}
