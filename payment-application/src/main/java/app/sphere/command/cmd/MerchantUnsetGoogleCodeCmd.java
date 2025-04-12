package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantUnsetGoogleCodeCmd {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 验证码
     */
    private String authCode;

    /**
     * 商户查询来源
     */
    private Integer querySource;

}
