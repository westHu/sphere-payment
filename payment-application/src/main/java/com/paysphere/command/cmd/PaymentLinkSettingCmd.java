package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class PaymentLinkSettingCmd {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 配置参数
     */
    private String paymentLinkSetting;

}
