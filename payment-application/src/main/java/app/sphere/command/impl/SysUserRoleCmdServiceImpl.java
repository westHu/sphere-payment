package app.sphere.command.impl;

import app.sphere.command.SysUserRoleCmdService;
import app.sphere.command.cmd.SysLoginCommand;
import app.sphere.command.cmd.SysRoleAddCommand;
import app.sphere.command.cmd.SysRoleUpdateCommand;
import app.sphere.command.cmd.SysUserAddCommand;
import app.sphere.command.cmd.SysUserUpdateCommand;
import app.sphere.command.dto.SysLoginDTO;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.SysRoleRepository;
import domain.sphere.repository.SysUserRepository;
import infrastructure.sphere.db.entity.SysRole;
import infrastructure.sphere.db.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import share.sphere.utils.JWTUtil;
import share.sphere.utils.dto.JWTTokenUserDTO;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SysUserRoleCmdServiceImpl implements SysUserRoleCmdService {

    @Resource
    private SysUserRepository sysUserRepository;
    
    @Resource
    private SysRoleRepository sysRoleRepository;
    
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

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
    public void addUser(SysUserAddCommand command) {
        log.info("添加系统用户, command={}", JSONUtil.toJsonStr(command));

        // 1. 参数校验
        Assert.notBlank(command.getUsername(), () -> new PaymentException(ExceptionCode.PARAM_MISSING, "用户名不能为空"));
        Assert.notBlank(command.getPassword(), () -> new PaymentException(ExceptionCode.PARAM_MISSING, "密码不能为空"));

        // 2. 检查用户名是否已存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, command.getUsername());
        SysUser existUser = sysUserRepository.getOne(queryWrapper);
        Assert.isNull(existUser, () -> new PaymentException(ExceptionCode.USER_HAS_EXIST, command.getUsername()));

        // 3. 创建用户对象
        SysUser user = new SysUser();
        user.setUsername(command.getUsername());
        user.setPassword(passwordEncoder.encode(command.getPassword()));
        user.setRealName(command.getRealName());
        user.setMobile(command.getMobile());
        user.setStatus(true); // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 4. 保存用户
        boolean success = sysUserRepository.save(user);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "添加用户失败"));
    }

    @Override
    public void updateUser(SysUserUpdateCommand command) {
        log.info("更新系统用户, command={}", JSONUtil.toJsonStr(command));

        // 1. 参数校验
        Assert.notBlank(command.getUsername(), () -> new PaymentException(ExceptionCode.PARAM_MISSING, "用户名不能为空"));

        // 2. 查询用户是否存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, command.getUsername());
        SysUser user = sysUserRepository.getOne(queryWrapper);
        Assert.notNull(user, () -> new PaymentException(ExceptionCode.USER_NOT_FOUND, command.getUsername()));

        // 3. 构建更新条件
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(SysUser::getUsername, command.getUsername());

        // 4. 更新密码（如果提供了新密码）
        if (command.getPassword() != null && !command.getPassword().isEmpty()) {
            updateWrapper.lambda()
                    .set(SysUser::getPassword, passwordEncoder.encode(command.getPassword()));
        }

        // 5. 更新其他字段
        if (command.getRealName() != null) {
            updateWrapper.lambda()
                    .set(SysUser::getRealName, command.getRealName());
        }

        if (command.getStatus() != null) {
            updateWrapper.set("status", command.getStatus());
        }

        // 6. 设置更新时间
        updateWrapper.lambda()
                .set(SysUser::getUpdateTime, LocalDateTime.now());

        // 7. 执行更新
        boolean success = sysUserRepository.update(updateWrapper);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "更新用户失败"));
    }

    @Override
    public void addRole(SysRoleAddCommand command) {
        log.info("添加系统角色, command={}", JSONUtil.toJsonStr(command));

        // 1. 参数校验
        Assert.notBlank(command.getRoleCode(), () -> new PaymentException(ExceptionCode.PARAM_MISSING, "角色编码不能为空"));
        Assert.notBlank(command.getRoleName(), () -> new PaymentException(ExceptionCode.PARAM_MISSING, "角色名称不能为空"));

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
        role.setUpdateTime(LocalDateTime.now());

        // 4. 保存角色
        boolean success = sysRoleRepository.save(role);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "添加角色失败"));
    }

    @Override
    public void updateRole(SysRoleUpdateCommand command) {
        log.info("更新系统角色, command={}", JSONUtil.toJsonStr(command));

        // 1. 参数校验
        Assert.notBlank(command.getRoleCode(), () -> new PaymentException(ExceptionCode.PARAM_MISSING, "角色编码不能为空"));

        // 2. 查询角色是否存在
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRole::getRoleCode, command.getRoleCode());
        SysRole role = sysRoleRepository.getOne(queryWrapper);
        Assert.notNull(role, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "角色不存在: " + command.getRoleCode()));

        // 3. 构建更新条件
        UpdateWrapper<SysRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(SysRole::getRoleCode, command.getRoleCode());

        // 4. 更新角色名称（如果提供了新名称）
        if (command.getRoleName() != null && !command.getRoleName().isEmpty()) {
            updateWrapper.lambda()
                    .set(SysRole::getRoleName, command.getRoleName());
        }

        // 5. 更新角色描述（如果提供了新描述）
        if (command.getDescription() != null) {
            updateWrapper.lambda()
                    .set(SysRole::getDescription, command.getDescription());
        }

        // 6. 更新状态
        updateWrapper.set("status", command.isStatus());

        // 7. 更新排序（如果提供了新排序）
        if (command.getSort() != null) {
            updateWrapper.lambda()
                    .set(SysRole::getSort, command.getSort());
        }

        // 8. 设置更新时间
        updateWrapper.lambda()
                .set(SysRole::getUpdateTime, LocalDateTime.now());

        // 9. 执行更新
        boolean success = sysRoleRepository.update(updateWrapper);
        Assert.isTrue(success, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "更新角色失败"));
    }
}
