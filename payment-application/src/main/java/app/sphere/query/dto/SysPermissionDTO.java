package app.sphere.query.dto;

import lombok.Data;

@Data
public class SysPermissionDTO {
    /**
     * 权限ID
     */
    private Long id;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限类型(1-菜单 2-按钮)
     */
    private Integer type;

    /**
     * 权限路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态(0-禁用 1-启用)
     */
    private boolean status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}
