package app.sphere.query;

import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.SysPermissionDTO;
import app.sphere.query.dto.SysRoleDTO;
import app.sphere.query.dto.SysUserDTO;
import app.sphere.query.param.SysPermissionParam;
import app.sphere.query.param.SysRolePageParam;
import app.sphere.query.param.SysUserPageParam;

import java.util.List;

public interface SysUserRoleQueryService {

    List<SysPermissionDTO> getMenu();

    PageDTO<SysUserDTO> pageUserList(SysUserPageParam param);

    SysUserDTO getUser(Long id);

    PageDTO<SysRoleDTO> pageRoleList(SysRolePageParam param);

    SysRoleDTO getRole(Long id);

    List<SysPermissionDTO> getPermissionList(SysPermissionParam param);

    SysPermissionDTO getPermission(Long id);
}
