package app.sphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class SysRoleAssignPermissionCommand {

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 权限Id列表
     */
    private List<Long> permissionIdList;
}
