package app.sphere.command.cmd;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentLinkSettingCmd extends MerchantIdCommand {

    /**
     * 配置参数
     */
    private String paymentLinkSetting;

}
