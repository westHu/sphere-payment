package api.sphere.controller.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantOperatorUpdateReq extends IdReq {

    /**
     * 密码
     */
    @Length(min = 8, max = 16, message = "password must be 8 to 16 character")
    private String password;

    /**
     * 角色
     */
    private Long role;
}
