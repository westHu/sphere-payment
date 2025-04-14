package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper接口
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
} 