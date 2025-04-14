package api.sphere.controller.front.admin;

import api.sphere.controller.request.SysLoginReq;
import api.sphere.controller.request.SysPermissionReq;
import api.sphere.controller.request.SysRoleAddReq;
import api.sphere.controller.request.SysRoleAssignPermissionReq;
import api.sphere.controller.request.SysRolePageReq;
import api.sphere.controller.request.SysRoleUpdateReq;
import api.sphere.controller.request.SysUserAddReq;
import api.sphere.controller.request.SysUserPageReq;
import api.sphere.controller.request.SysUserUpdateReq;
import api.sphere.convert.SysUserRoleConverter;
import app.sphere.command.SysUserRoleCmdService;
import app.sphere.command.cmd.SysLoginCommand;
import app.sphere.command.cmd.SysRoleAddCommand;
import app.sphere.command.cmd.SysRoleAssignPermissionCommand;
import app.sphere.command.cmd.SysRoleUpdateCommand;
import app.sphere.command.cmd.SysUserAddCommand;
import app.sphere.command.cmd.SysUserAssignRoleCommand;
import app.sphere.command.cmd.SysUserUpdateCommand;
import app.sphere.command.dto.SysLoginDTO;
import app.sphere.query.SysUserRoleQueryService;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.SysPermissionDTO;
import app.sphere.query.dto.SysRoleDTO;
import app.sphere.query.dto.SysUserDTO;
import app.sphere.query.param.SysPermissionParam;
import app.sphere.query.param.SysRolePageParam;
import app.sphere.query.param.SysUserPageParam;
import cn.hutool.json.JSONUtil;
import domain.sphere.repository.SysPermissionRepository;
import domain.sphere.repository.SysRoleRepository;
import domain.sphere.repository.SysUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台基础控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Resource
    SysUserRoleCmdService sysUserRoleCmdService;
    @Resource
    SysUserRoleQueryService sysUserRoleQueryService;
    @Resource
    SysUserRepository sysUserRepository;
    @Resource
    SysRoleRepository sysRoleRepository;
    @Resource
    SysPermissionRepository sysPermissionRepository;
    @Resource
    SysUserRoleConverter sysUserRoleConverter;

    /**
     * 管理员登录
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<SysLoginDTO> sysLogin(@RequestBody @Validated SysLoginReq req) {
        log.info("管理员登录, req={}", JSONUtil.toJsonStr(req));
        SysLoginCommand cmd = sysUserRoleConverter.convertSysLoginCmd(req);
        SysLoginDTO sysLoginDTO = sysUserRoleCmdService.sysLogin(cmd);
        return Result.ok(sysLoginDTO);
    }

    /**
     * 管理员登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("管理员登出, req={}", LocalDateTime.now());
        return Result.ok(null);
    }

    /**
     * 获取管理员菜单
     * @return 菜单列表
     */
    @PostMapping("/getMenu")
    public Result<List<SysPermissionDTO>> getMenu() {
        log.info("管理员菜单, req={}", LocalDateTime.now());
        List<SysPermissionDTO> menuList = sysUserRoleQueryService.getMenu();
        return Result.ok(menuList);
    }

    // 用户管理接口
    @PostMapping("/addUser")
    public Result<Boolean> addUser(@RequestBody SysUserAddReq req) {
        log.info("新增用户管理接口, req={}", JSONUtil.toJsonStr(req));
        SysUserAddCommand command = sysUserRoleConverter.convertSysUserAddCommand(req);
        boolean addUser = sysUserRoleCmdService.addUser(command);
        return Result.ok(addUser);
    }

    @PostMapping("/updateUser")
    public Result<Boolean> updateUser(@RequestBody SysUserUpdateReq req) {
        log.info("更新用户管理接口, req={}", JSONUtil.toJsonStr(req));
        SysUserUpdateCommand command = sysUserRoleConverter.convertSysUserUpdateCommand(req);
        boolean updateUser = sysUserRoleCmdService.updateUser(command);
        return Result.ok(updateUser);
    }

    @PostMapping("/assignRole")
    public Result<Boolean> assignRole(@RequestBody SysUserUpdateReq req) {
        log.info("更新用户管理接口, req={}", JSONUtil.toJsonStr(req));
        SysUserAssignRoleCommand command = sysUserRoleConverter.convertSysUserAssignRoleCommand(req);
        boolean updateUser = sysUserRoleCmdService.assignRole(command);
        return Result.ok(updateUser);
    }

    @PostMapping("/pageUserList")
    public PageResult<SysUserDTO> pageUserList(@RequestParam SysUserPageReq req) {
        log.info("分页查询用户管理接口, req={}", JSONUtil.toJsonStr(req));
        SysUserPageParam param = sysUserRoleConverter.convertSysUserPageParam(req);
        PageDTO<SysUserDTO> pageDTO = sysUserRoleQueryService.pageUserList(param);
        return PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData());
    }

    @PostMapping("/getUser")
    public Result<SysUserDTO> getUser(@RequestParam Long id) {
        log.info("查询用户管理接口, req={}", id);
        SysUserDTO userDTO = sysUserRoleQueryService.getUser(id);
        return Result.ok(userDTO);
    }

    // 角色管理接口
    @PostMapping("/addRole")
    public Result<Boolean> addRole(@RequestBody SysRoleAddReq req) {
        log.info("新增角色管理接口, req={}", JSONUtil.toJsonStr(req));
        SysRoleAddCommand command = sysUserRoleConverter.convertSysRoleAddCommand(req);
        boolean addRole = sysUserRoleCmdService.addRole(command);
        return Result.ok(addRole);
    }

    @PostMapping("/updateRole")
    public Result<Boolean> updateRole(@RequestBody SysRoleUpdateReq req) {
        log.info("更新角色管理接口, req={}", JSONUtil.toJsonStr(req));
        SysRoleUpdateCommand command = sysUserRoleConverter.convertSysRoleUpdateCommand(req);
        boolean updateRole = sysUserRoleCmdService.updateRole(command);
        return Result.ok(updateRole);
    }

    @PostMapping("/assignPermission")
    public Result<Boolean> assignPermission(@RequestBody SysRoleAssignPermissionReq req) {
        log.info("更新角色管理接口, req={}", JSONUtil.toJsonStr(req));
        SysRoleAssignPermissionCommand command = sysUserRoleConverter.convertSysRoleAssignPermissionCommand(req);
        boolean updateRole = sysUserRoleCmdService.assignPermission(command);
        return Result.ok(updateRole);
    }

    @PostMapping("/pageRoleList")
    public PageResult<SysRoleDTO> pageRoleList(@RequestBody SysRolePageReq req) {
        log.info("分页查询角色管理接口, req={}", JSONUtil.toJsonStr(req));
        SysRolePageParam param = sysUserRoleConverter.convertSysRolePageParam(req);
        PageDTO<SysRoleDTO> pageDTO = sysUserRoleQueryService.pageRoleList(param);
        return PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData());
    }

    @PostMapping("/getRole")
    public Result<SysRoleDTO> getRole(@RequestParam Long id) {
        log.info("查询角色管理接口, req={}", id);
        SysRoleDTO roleDTO = sysUserRoleQueryService.getRole(id);
        return Result.ok(roleDTO);
    }

    @PostMapping("/getPermissionList")
    public Result<List<SysPermissionDTO>> getPermissionList(@RequestBody SysPermissionReq req) {
        log.info("查询权限管理接口, req={}", JSONUtil.toJsonStr(req));
        SysPermissionParam param = sysUserRoleConverter.convertSysPermissionParam(req);
        List<SysPermissionDTO> permissionDTOList = sysUserRoleQueryService.getPermissionList(param);
        return Result.ok(permissionDTOList);
    }

    @PostMapping("/getPermission")
    public Result<SysPermissionDTO> getPermission(@RequestParam Long id) {
        log.info("查询权限管理接口, req={}", id);
        SysPermissionDTO permissionDTO = sysUserRoleQueryService.getPermission(id);
        return Result.ok(permissionDTO);
    }

}
