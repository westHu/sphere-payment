package api.sphere.controller.response;

import lombok.Data;

/**
 * 密码重置
 */
@Data
public class MerchantPasswordResetVO {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 新密码
     */
    private String newPassword;

}
