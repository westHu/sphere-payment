package api.sphere.controller.request;

import lombok.Data;

@Data
public class SysRoleUpdateReq {
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
