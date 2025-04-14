package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * 密码重置
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantPasswordForgetReq extends QuerySourceReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "password is required")
    private String password;


}
