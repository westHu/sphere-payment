package infrastructure.sphere.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import infrastructure.sphere.db.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
} 