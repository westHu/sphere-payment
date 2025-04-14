package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SysRoleAssignPermissionReq {

    /**
     * 角色编码
     */
    @NotBlank(message = "roleCode is required")
    private String roleCode;

    /**
     * 权限Id列表
     */
    private List<Long> permissionIdList;
}
