package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantSetGoogleCodeCmd {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 验证秘钥
     */
    private String loginAuth;

    /**
     * 验证码
     */
    private String authCode;

}
