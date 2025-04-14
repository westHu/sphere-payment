package app.sphere.command;


import app.sphere.command.cmd.MerchantConfigUpdateCmd;
import app.sphere.command.cmd.PaymentLinkSettingCmd;

public interface MerchantConfigCmdService {

    boolean updateMerchantConfig(MerchantConfigUpdateCmd command);

    boolean updatePaymentLinkSetting(PaymentLinkSettingCmd command);
}
