package app.sphere.query.param;

import lombok.Data;

@Data
public class SysPermissionParam {

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;
}
