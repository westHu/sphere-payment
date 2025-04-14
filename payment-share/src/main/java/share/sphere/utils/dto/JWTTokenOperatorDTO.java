package share.sphere.utils.dto;

import lombok.Data;

@Data
public class JWTTokenOperatorDTO {

    /**
     * 用户名
     * 操作员的登录用户名，全局唯一
     */
    private String username;

}
