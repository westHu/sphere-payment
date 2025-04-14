package api.sphere.controller.request;

import lombok.Data;

@Data
public class SysUserUpdateReq {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 状态
     */
    private Boolean status;
}
