package domain.sphere.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import domain.sphere.repository.SettleOrderRepository;
import domain.sphere.repository.SysUserRepository;
import infrastructure.sphere.db.entity.SettleOrder;
import infrastructure.sphere.db.entity.SysUser;
import infrastructure.sphere.db.mapper.SettleOrderMapper;
import infrastructure.sphere.db.mapper.SysUserMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 用户Repository实现类
 */
@Repository
public class SysUserRepositoryImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserRepository {
} 