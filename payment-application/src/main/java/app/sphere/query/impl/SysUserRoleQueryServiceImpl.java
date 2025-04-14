package app.sphere.query.impl;

import app.sphere.query.SysUserRoleQueryService;
import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import domain.sphere.repository.*;
import infrastructure.sphere.db.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysUserRoleQueryServiceImpl implements SysUserRoleQueryService {

    @Resource
    SysUserRepository sysUserRepository;
    @Resource
    SysRoleRepository sysRoleRepository;
    @Resource
    SysPermissionRepository sysPermissionRepository;

    @Override
    public List<SysPermissionDTO> getMenu() {
        List<SysPermission> permissionList = sysPermissionRepository.list();
        return permissionList.stream().map(e -> {
            SysPermissionDTO permissionDTO = new SysPermissionDTO();
            BeanUtils.copyProperties(e, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public PageDTO<SysUserDTO> pageUserList(SysUserPageParam param) {
        log.info("分页查询用户列表, param={}", JSONUtil.toJsonStr(param));

        // 1. 构建查询条件
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        if (param.getUsername() != null && !param.getUsername().isEmpty()) {
            queryWrapper.lambda().like(SysUser::getUsername, param.getUsername());
        }

        // 2. 执行分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(param.getPageNum(), param.getPageSize());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> userPage = sysUserRepository.page(page, queryWrapper);

        // 3. 转换为DTO
        List<SysUserDTO> userDTOList = userPage.getRecords().stream().map(user -> {
            SysUserDTO userDTO = new SysUserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        }).collect(Collectors.toList());

        // 4. 构建返回结果
        PageDTO<SysUserDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(userPage.getTotal());
        pageDTO.setData(userDTOList);
        pageDTO.setCurrent((long) param.getPageNum());

        return pageDTO;
    }

    @Override
    public SysUserDTO getUser(Long id) {
        log.info("获取用户信息, id={}", id);

        // 1. 参数校验
        if (id == null) {
            throw new PaymentException(ExceptionCode.PARAM_MISSING, "用户ID不能为空");
        }

        // 2. 查询用户
        SysUser user = sysUserRepository.getById(id);
        if (user == null) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "用户不存在: " + id);
        }

        // 3. 转换为DTO
        SysUserDTO userDTO = new SysUserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public PageDTO<SysRoleDTO> pageRoleList(SysRolePageParam param) {
        log.info("分页查询角色列表, param={}", JSONUtil.toJsonStr(param));

        // 1. 构建查询条件
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        if (param.getRoleCode() != null && !param.getRoleCode().isEmpty()) {
            queryWrapper.lambda().like(SysRole::getRoleCode, param.getRoleCode());
        }
        if (param.getRoleName() != null && !param.getRoleName().isEmpty()) {
            queryWrapper.lambda().like(SysRole::getRoleName, param.getRoleName());
        }
        queryWrapper.eq("status", param.isStatus() ? 1 : 0);

        // 2. 执行分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysRole> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(param.getPageNum(), param.getPageSize());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysRole> rolePage = sysRoleRepository.page(page, queryWrapper);

        // 3. 转换为DTO
        List<SysRoleDTO> roleDTOList = rolePage.getRecords().stream().map(role -> {
            SysRoleDTO roleDTO = new SysRoleDTO();
            BeanUtils.copyProperties(role, roleDTO);
            return roleDTO;
        }).collect(Collectors.toList());

        // 4. 构建返回结果
        PageDTO<SysRoleDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(rolePage.getTotal());
        pageDTO.setData(roleDTOList);
        pageDTO.setCurrent((long) param.getPageNum());

        return pageDTO;
    }

    @Override
    public SysRoleDTO getRole(Long id) {
        log.info("获取角色信息, id={}", id);

        // 1. 参数校验
        if (id == null) {
            throw new PaymentException(ExceptionCode.PARAM_MISSING, "角色ID不能为空");
        }

        // 2. 查询角色
        SysRole role = sysRoleRepository.getById(id);
        if (role == null) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "角色不存在: " + id);
        }

        // 3. 转换为DTO
        SysRoleDTO roleDTO = new SysRoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        return roleDTO;
    }

    @Override
    public List<SysPermissionDTO> getPermissionList(SysPermissionParam param) {
        log.info("获取权限列表, param={}", JSONUtil.toJsonStr(param));

        // 1. 构建查询条件
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
        if (param.getCode() != null && !param.getCode().isEmpty()) {
            queryWrapper.lambda().like(SysPermission::getCode, param.getCode());
        }
        if (param.getName() != null && !param.getName().isEmpty()) {
            queryWrapper.lambda().like(SysPermission::getName, param.getName());
        }

        // 2. 执行查询
        List<SysPermission> permissionList = sysPermissionRepository.list(queryWrapper);

        // 3. 转换为DTO
        return permissionList.stream().map(permission -> {
            SysPermissionDTO permissionDTO = new SysPermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public SysPermissionDTO getPermission(Long id) {
        log.info("获取权限信息, id={}", id);

        // 1. 参数校验
        if (id == null) {
            throw new PaymentException(ExceptionCode.PARAM_MISSING, "权限ID不能为空");
        }

        // 2. 查询权限
        SysPermission permission = sysPermissionRepository.getById(id);
        if (permission == null) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "权限不存在: " + id);
        }

        // 3. 转换为DTO
        SysPermissionDTO permissionDTO = new SysPermissionDTO();
        BeanUtils.copyProperties(permission, permissionDTO);
        return permissionDTO;
    }
}
