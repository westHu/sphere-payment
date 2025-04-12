package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantOperatorAddCmd {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 操作员姓名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 头像
     */
    private String icon;

    /**
     * 描述
     */
    private String desc;
}
