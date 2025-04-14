package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

}
