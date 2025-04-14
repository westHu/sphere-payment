package infrastructure.sphere.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {
    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态(0-禁用 1-启用)
     */
    private boolean status;

    /**
     * 排序
     */
    private Integer sort;
} 