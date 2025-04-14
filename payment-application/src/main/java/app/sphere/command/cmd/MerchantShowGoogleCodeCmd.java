package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantShowGoogleCodeCmd {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 用户名
     */
    private String username;

}
