package app.sphere.command;

import app.sphere.command.cmd.SysLoginCommand;
import app.sphere.command.cmd.SysRoleAddCommand;
import app.sphere.command.cmd.SysRoleAssignPermissionCommand;
import app.sphere.command.cmd.SysRoleUpdateCommand;
import app.sphere.command.cmd.SysUserAddCommand;
import app.sphere.command.cmd.SysUserAssignRoleCommand;
import app.sphere.command.cmd.SysUserUpdateCommand;
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
