package app.sphere.command.dto;

import infrastructure.sphere.db.entity.SysUser;
import lombok.Data;

@Data
public class SysLoginDTO {

    /**
     * 访问秘钥
     */
    private String accessToken;

    /**
     * 基本信息
     */
    private SysUser user;
}
