package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
} 