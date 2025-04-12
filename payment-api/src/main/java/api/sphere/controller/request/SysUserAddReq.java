package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysUserAddReq {
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

    /**
     * 真实姓名
     */
    @NotBlank(message = "realName is required")
    private String realName;

    /**
     * 手机号
     */
    @NotBlank(message = "mobile is required")
    private String mobile;
}
