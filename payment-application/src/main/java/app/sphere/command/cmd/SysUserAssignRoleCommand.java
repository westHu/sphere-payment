package app.sphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class SysUserAssignRoleCommand {

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色ID列表
     */
    private List<Long> roleIdList;

}
