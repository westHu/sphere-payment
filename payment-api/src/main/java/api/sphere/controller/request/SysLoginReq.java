package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author dh
 */
@Data
public class SysLoginReq {

    /**
     * 登录密码
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "password is required")
    private String password;

}
