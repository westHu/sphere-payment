package api.sphere.controller.request;

import lombok.Data;

@Data
public class SysRolePageReq {
    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 状态(0-禁用 1-启用)
     */
    private boolean status;

}
