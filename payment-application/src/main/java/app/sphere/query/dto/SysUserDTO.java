package app.sphere.query.dto;

import lombok.Data;

@Data
public class SysUserDTO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 状态(0-禁用 1-启用)
     */
    private boolean status;

    /**
     * 最后登录时间
     */
    private String lastLoginTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}
