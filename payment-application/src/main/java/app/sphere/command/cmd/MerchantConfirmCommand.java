package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantConfirmCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 主题
     */
    private String subject;

    /**
     * 验证key
     */
    private String token;

    /**
     * 时间
     */
    private String time;
}
