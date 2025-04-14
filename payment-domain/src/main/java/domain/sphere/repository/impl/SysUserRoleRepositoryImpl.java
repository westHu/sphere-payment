package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SysUserRepository;
import domain.sphere.repository.SysUserRoleRepository;
import infrastructure.sphere.db.entity.SysUser;
import infrastructure.sphere.db.entity.SysUserRole;
import infrastructure.sphere.db.mapper.SysUserMapper;
import infrastructure.sphere.db.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户Repository实现类
 */
@Repository
public class SysUserRoleRepositoryImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleRepository {
} 