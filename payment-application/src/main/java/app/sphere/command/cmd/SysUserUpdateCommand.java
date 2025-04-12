package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SysUserUpdateCommand {
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
