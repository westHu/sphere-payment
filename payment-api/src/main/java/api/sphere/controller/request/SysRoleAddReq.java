package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysRoleAddReq {

    /**
     * 角色编码
     */
    @NotBlank(message = "roleCode is required")
    private String roleCode;

    /**
     * 角色名称
     */
    @NotBlank(message = "roleName is required")
    private String roleName;

    /**
     * 角色描述
     */
    @NotBlank(message = "description is required")
    private String description;
}
