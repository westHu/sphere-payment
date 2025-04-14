package api.sphere.controller.request;

import lombok.Data;

@Data
public class SysPermissionReq {

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;
}
