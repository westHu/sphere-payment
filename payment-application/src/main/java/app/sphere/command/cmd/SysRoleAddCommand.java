package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SysRoleAddCommand {

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

}
