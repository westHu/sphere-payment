package app.sphere.command.impl;

import app.sphere.command.SysUserRoleCmdService;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.SysLoginDTO;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.*;
import infrastructure.sphere.db.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import share.sphere.utils.JWTUtil;
import share.sphere.utils.dto.JWTTokenUserDTO;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class SysUserRoleCmdServiceImpl implements SysUserRoleCmdService {

    @Resource
    SysUserRepository sysUserRepository;
    @Resource
    SysRoleRepository sysRoleRepository;
    @Resource
    SysPermissionRepository sysPermissionRepository;
    @Resource
    SysUserRoleRepository sysUserRoleRepository;
    @Resource
    SysRolePermissionRepository sysRolePermissionRepository;
    @Resource
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public SysLoginDTO sysLogin(SysLoginCommand cmd) {
        log.info("系统用户登录, cmd={}", JSONUtil.toJsonStr(cmd));

        // 1. 根据用户名查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, cmd.getUsername());
        SysUser user = sysUserRepository.getOne(queryWrapper);
        
        // 2. 验证用户是否存在
        Assert.notNull(user, () -> new PaymentException(ExceptionCode.USER_NOT_FOUND, cmd.getUsername()));
        
        // 3. 验证用户状态
        Assert.isTrue(user.isStatus(), () -> new PaymentException(ExceptionCode.USER_DISABLED, cmd.getUsername()));
        
        // 4. 验证密码
        boolean matches = passwordEncoder.matches(cmd.getPassword(), user.getPassword());
        Assert.isTrue(matches, () -> new PaymentException(ExceptionCode.USER_LOGIN_ERROR, cmd.getUsername()));

        // 5. 更新最后登录时间
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(SysUser::getLastLoginTime, LocalDateTime.now())
                .eq(SysUser::getId, user.getId());
        sysUserRepository.update(updateWrapper);

        // 6. 生成JWT token
        JWTTokenUserDTO jwtTokenUserDTO = new JWTTokenUserDTO();
        jwtTokenUserDTO.setUsername(user.getUsername());
        String accessToken = JWTUtil.createToken(null, null, jwtTokenUserDTO);

        // 7. 返回登录结果
        SysLoginDTO sysLoginDTO = new SysLoginDTO();
        sysLoginDTO.setAccessToken(accessToken);
        sysLoginDTO.setUser(user);

        return sysLoginDTO;
    }

    @Override
    public boolean addUser(SysUserAddCommand command) {
        log.info("添加系统用户, command={}", JSONUtil.toJsonStr(command));

        // 2. 检查用户名是否已存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, command.getUsername());
        SysUser existUser = sysUserRepository.getOne(queryWrapper);
        Assert.isNull(existUser, () -> new PaymentException(ExceptionCode.USER_HAS_EXIST, command.getUsername()));

        // 3. 创建用户对象
        String password = passwordEncoder.encode(MD5.create().digestHex16(command.getPassword()));
        SysUser user = new SysUser();
        user.setUsername(command.getUsername());
        user.setPassword(password);
        user.setRealName(command.getRealName());
        user.setMobile(command.getMobile());
        user.setStatus(true);
        user.setCreateTime(LocalDateTime.now());

        // 4. 保存用户
        boolean success = sysUserRepository.save(user);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "添加用户失败"));
        return success;
    }

    @Override
    public boolean updateUser(SysUserUpdateCommand command) {
        log.info("更新系统用户, command={}", JSONUtil.toJsonStr(command));

        // 2. 查询用户是否存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, command.getUsername());
        SysUser user = sysUserRepository.getOne(queryWrapper);
        Assert.notNull(user, () -> new PaymentException(ExceptionCode.USER_NOT_FOUND, command.getUsername()));

        // 3. 构建更新条件
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(Objects.nonNull(command.getRealName()), SysUser::getRealName, command.getRealName())
                .set(Objects.nonNull(command.getStatus()), SysUser::isStatus, command.getStatus())
                .set(Objects.nonNull(command.getPassword()), SysUser::getPassword,
                        passwordEncoder.encode(MD5.create().digestHex16(command.getPassword())));
        // 7. 执行更新
        boolean success = sysUserRepository.update(updateWrapper);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "更新用户失败"));
        return success;
    }

    @Override
    public boolean assignRole(SysUserAssignRoleCommand command) {
        log.info("assignRole, command={}", JSONUtil.toJsonStr(command));

        // 2. 查询用户是否存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, command.getUsername());
        SysUser user = sysUserRepository.getOne(queryWrapper);
        Assert.notNull(user, () -> new PaymentException(ExceptionCode.USER_NOT_FOUND, command.getUsername()));

        List<SysRole> roleList = sysRoleRepository.listByIds(command.getRoleIdList());
        List<Long> roleIdList = roleList.stream().map(BaseEntity::getId).toList();
        List<SysUserRole> userRoleList = roleIdList.stream().map(e -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(e);
            return userRole;
        }).toList();
        return sysUserRoleRepository.saveBatch(userRoleList);
    }

    @Override
    public boolean addRole(SysRoleAddCommand command) {
        log.info("添加系统角色, command={}", JSONUtil.toJsonStr(command));

        // 2. 检查角色编码是否已存在
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRole::getRoleCode, command.getRoleCode());
        SysRole existRole = sysRoleRepository.getOne(queryWrapper);
        Assert.isNull(existRole, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "角色编码已存在: " + command.getRoleCode()));

        // 3. 创建角色对象
        SysRole role = new SysRole();
        role.setRoleCode(command.getRoleCode());
        role.setRoleName(command.getRoleName());
        role.setDescription(command.getDescription());
        role.setStatus(true); // 默认启用
        role.setSort(0); // 默认排序
        role.setCreateTime(LocalDateTime.now());

        // 4. 保存角色
        boolean success = sysRoleRepository.save(role);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "添加角色失败"));
        return success;
    }

    @Override
    public boolean updateRole(SysRoleUpdateCommand command) {
        log.info("更新系统角色, command={}", JSONUtil.toJsonStr(command));

        // 2. 查询角色是否存在
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRole::getRoleCode, command.getRoleCode()).last(LIMIT_1);
        SysRole role = sysRoleRepository.getOne(queryWrapper);
        Assert.notNull(role, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "角色不存在: " + command.getRoleCode()));

        // 3. 构建更新条件
        UpdateWrapper<SysRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(Objects.nonNull(command.getRoleName()), SysRole::getRoleName, command.getRoleName())
                .set(Objects.nonNull(command.getDescription()), SysRole::getDescription, command.getDescription())
                .set(Objects.nonNull(command.getStatus()), SysRole::isStatus, command.getStatus())
                .eq(SysRole::getRoleCode, command.getRoleCode());
        // 9. 执行更新
        boolean success = sysRoleRepository.update(updateWrapper);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "更新角色失败"));
        return success;
    }

    @Override
    public boolean assignPermission(SysRoleAssignPermissionCommand command) {
        log.info("assignRole, command={}", JSONUtil.toJsonStr(command));

        // 2. 查询用户是否存在
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRole::getRoleCode, command.getRoleCode()).last(LIMIT_1);
        SysRole role = sysRoleRepository.getOne(queryWrapper);
        Assert.notNull(role, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "角色不存在: " + command.getRoleCode()));

        List<SysPermission> permissionList = sysPermissionRepository.listByIds(command.getPermissionIdList());
        List<Long> permissionIdList = permissionList.stream().map(BaseEntity::getId).toList();
        List<SysRolePermission> rolePermissionList = permissionIdList.stream().map(e -> {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(e);
            return rolePermission;
        }).toList();
        return sysRolePermissionRepository.saveBatch(rolePermissionList);

    }
}
