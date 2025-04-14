package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
} 