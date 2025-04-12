package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SysLoginCommand {

    /**
     * 用户名. 唯一
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
