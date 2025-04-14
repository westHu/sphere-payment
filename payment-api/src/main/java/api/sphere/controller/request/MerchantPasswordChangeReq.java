package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码重置
 */
@Data
public class MerchantPasswordChangeReq extends QuerySourceReq {

    /**
     * 用户名
     */
    @NotBlank(message = "username is required")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "oldPassword is required")
    private String oldPassword;

    /**
     * 密码
     */
    @NotBlank(message = "newPassword is required")
    private String newPassword;


}
