package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SysPermissionRepository;
import infrastructure.sphere.db.entity.SysPermission;
import infrastructure.sphere.db.mapper.SysPermissionMapper;
import org.springframework.stereotype.Repository;

/**
 * 权限Repository实现类
 */
@Repository
public class SysPermissionRepositoryImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements SysPermissionRepository {
} 