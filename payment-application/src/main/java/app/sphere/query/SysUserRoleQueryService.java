package app.sphere.query;

import app.sphere.query.dto.*;
import app.sphere.query.param.*;

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
