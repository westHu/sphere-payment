package app.sphere.command;

import app.sphere.command.cmd.*;
import app.sphere.command.dto.SysLoginDTO;

public interface SysUserRoleCmdService {

    SysLoginDTO sysLogin(SysLoginCommand cmd);

    boolean addUser(SysUserAddCommand command);

    boolean updateUser(SysUserUpdateCommand command);

    boolean assignRole(SysUserAssignRoleCommand command);

    boolean addRole(SysRoleAddCommand command);

    boolean updateRole(SysRoleUpdateCommand command);

    boolean assignPermission(SysRoleAssignPermissionCommand command);
}
