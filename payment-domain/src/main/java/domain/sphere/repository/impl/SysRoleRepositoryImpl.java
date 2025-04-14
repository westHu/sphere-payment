package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SysRoleRepository;
import infrastructure.sphere.db.entity.SysRole;
import infrastructure.sphere.db.mapper.SysRoleMapper;
import org.springframework.stereotype.Repository;

/**
 * 角色Repository实现类
 */
@Repository
public class SysRoleRepositoryImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleRepository {
} 