package app.sphere.command.cmd;

import lombok.Data;

@Data
public class GoogleAccessTokenCommand {

    /**
     * code
     */
    private String code;

    /**
     * 客户端ID
     */
    private String client_id;

    /**
     * 客户端秘钥
     */
    private String client_secret;

    /**
     * 授权类型
     */
    private String grant_type = "authorization_code";

    /**
     * 回调地址
     */
    private String redirect_uri;

}
