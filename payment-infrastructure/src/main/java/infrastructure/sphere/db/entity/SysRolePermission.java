package infrastructure.sphere.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRolePermission extends BaseEntity {
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permissionId;
} 