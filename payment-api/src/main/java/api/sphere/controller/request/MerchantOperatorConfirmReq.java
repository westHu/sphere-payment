package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantOperatorConfirmReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 操作员姓名
     */
    @NotBlank(message = "merchantId is required")
    @Length(max = 32, message = "username max length 32-character")
    private String username;

    /**
     * 密码 此处加密后的密码
     */
    @NotBlank(message = "password is required")
    @Length(max = 64, message = "password max length 64-character")
    private String password;

    /**
     * 角色
     */
    @NotNull(message = "role is required")
    private Integer role;

    /**
     * subject
     */
    @NotBlank(message = "subject is required")
    private String subject;

    /**
     * token
     */
    @NotBlank(message = "token is required")
    private String token;

    /**
     * token
     */
    @NotBlank(message = "token is required")
    private String time;

    /**
     * 头像
     */
    private String icon;

    /**
     * 描述
     */
    private String desc;
}
