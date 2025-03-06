package com.paysphere.command;


import com.paysphere.command.cmd.MerchantConfigUpdateCmd;
import com.paysphere.command.cmd.PaymentLinkSettingCmd;

public interface MerchantConfigCmdService {

    boolean updateMerchantConfig(MerchantConfigUpdateCmd command);

    boolean updatePaymentLinkSetting(PaymentLinkSettingCmd command);
}
