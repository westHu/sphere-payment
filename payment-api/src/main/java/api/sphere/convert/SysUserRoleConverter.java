package api.sphere.convert;

import api.sphere.controller.request.*;
import app.sphere.command.cmd.*;
import app.sphere.query.param.*;
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
