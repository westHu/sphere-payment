package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SysRolePermissionRepository;
import infrastructure.sphere.db.entity.SysRolePermission;
import infrastructure.sphere.db.mapper.SysRolePermissionMapper;
import org.springframework.stereotype.Repository;

/**
 * 角色Repository实现类
 */
@Repository
public class SysRolePermissionRepositoryImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
        implements SysRolePermissionRepository {
} 