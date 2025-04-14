package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SysUserRepository;
import infrastructure.sphere.db.entity.SysUser;
import infrastructure.sphere.db.mapper.SysUserMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户Repository实现类
 */
@Repository
public class SysUserRepositoryImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserRepository {
} 