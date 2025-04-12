package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SysUserAssignRoleReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * 角色ID列表
     */
    private List<Long> roleIdList;

}
