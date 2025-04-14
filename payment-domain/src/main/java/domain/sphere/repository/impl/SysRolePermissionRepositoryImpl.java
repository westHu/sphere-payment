package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SysRolePermissionRepository;
import domain.sphere.repository.SysRoleRepository;
import infrastructure.sphere.db.entity.SysRole;
import infrastructure.sphere.db.entity.SysRolePermission;
import infrastructure.sphere.db.mapper.SysRoleMapper;
import infrastructure.sphere.db.mapper.SysRolePermissionMapper;
import org.springframework.stereotype.Repository;

/**
 * 角色Repository实现类
 */
@Repository
public class SysRolePermissionRepositoryImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
        implements SysRolePermissionRepository {
} 