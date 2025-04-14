package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantAccountUpdateJobCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 版本
     */
    private Integer version;

}
