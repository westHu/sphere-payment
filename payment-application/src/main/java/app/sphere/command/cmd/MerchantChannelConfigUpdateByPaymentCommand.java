package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantChannelConfigUpdateByPaymentCommand {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 商户渠道配置状态
     */
    private Boolean status;

}
