package api.sphere.controller.request;

import lombok.Data;

@Data
public class SysRoleAddReq {

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

}
