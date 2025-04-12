package app.sphere.command;

import app.sphere.command.cmd.SysLoginCommand;
import app.sphere.command.cmd.SysRoleAddCommand;
import app.sphere.command.cmd.SysRoleUpdateCommand;
import app.sphere.command.cmd.SysUserAddCommand;
import app.sphere.command.cmd.SysUserUpdateCommand;
import app.sphere.command.dto.SysLoginDTO;

public interface SysUserRoleCmdService {

    SysLoginDTO sysLogin(SysLoginCommand cmd);

    void addUser(SysUserAddCommand command);

    void updateUser(SysUserUpdateCommand command);

    void addRole(SysRoleAddCommand command);

    void updateRole(SysRoleUpdateCommand command);
}
