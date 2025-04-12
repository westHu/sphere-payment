package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantOperatorConfirmCommand {

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
     * 角色
     */
    private Long role;

    /**
     * subject
     */
    private String subject;

    /**
     * token
     */
    private String token;

    /**
     * token
     */
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
