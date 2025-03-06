package com.paysphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class MerchantQrisConfigAddCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 标题
     */
    private String qrisTitle;

    /**
     * qrisFrom
     */
    private List<String> qrisFrom;

    /**
     * WhatsApp 电话
     */
    private String whatsAppNotification;

}
