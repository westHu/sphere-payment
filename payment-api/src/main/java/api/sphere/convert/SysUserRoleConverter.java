package api.sphere.convert;

import api.sphere.controller.request.SysLoginReq;
import api.sphere.controller.request.SysPermissionReq;
import api.sphere.controller.request.SysRoleAddReq;
import api.sphere.controller.request.SysRoleAssignPermissionReq;
import api.sphere.controller.request.SysRolePageReq;
import api.sphere.controller.request.SysRoleUpdateReq;
import api.sphere.controller.request.SysUserAddReq;
import api.sphere.controller.request.SysUserPageReq;
import api.sphere.controller.request.SysUserUpdateReq;
import app.sphere.command.cmd.SysLoginCommand;
import app.sphere.command.cmd.SysRoleAddCommand;
import app.sphere.command.cmd.SysRoleAssignPermissionCommand;
import app.sphere.command.cmd.SysRoleUpdateCommand;
import app.sphere.command.cmd.SysUserAddCommand;
import app.sphere.command.cmd.SysUserAssignRoleCommand;
import app.sphere.command.cmd.SysUserUpdateCommand;
import app.sphere.query.param.SysPermissionParam;
import app.sphere.query.param.SysRolePageParam;
import app.sphere.query.param.SysUserPageParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface SysUserRoleConverter {

    SysLoginCommand convertSysLoginCmd(SysLoginReq req);

    SysUserAddCommand convertSysUserAddCommand(SysUserAddReq req);

    SysUserUpdateCommand convertSysUserUpdateCommand(SysUserUpdateReq req);

    SysUserPageParam convertSysUserPageParam(SysUserPageReq req);

    SysRoleAddCommand convertSysRoleAddCommand(SysRoleAddReq req);

    SysRoleUpdateCommand convertSysRoleUpdateCommand(SysRoleUpdateReq req);

    SysRolePageParam convertSysRolePageParam(SysRolePageReq req);

    SysPermissionParam convertSysPermissionParam(SysPermissionReq req);

    SysUserAssignRoleCommand convertSysUserAssignRoleCommand(SysUserUpdateReq req);

    SysRoleAssignPermissionCommand convertSysRoleAssignPermissionCommand(SysRoleAssignPermissionReq req);
}
