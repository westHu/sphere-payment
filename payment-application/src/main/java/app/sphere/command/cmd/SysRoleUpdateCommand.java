package app.sphere.command.cmd;

import lombok.Data;

@Data
public class SysRoleUpdateCommand {
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

    /**
     * 状态(0-禁用 1-启用)
     */
    private Boolean status;

    /**
     * 排序
     */
    private Integer sort;
}
