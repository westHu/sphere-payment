package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserPageParam extends PageParam {

    /**
     * 用户名
     */
    private String username;

}
