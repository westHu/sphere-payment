package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantApplyAccountCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 申请内容
     */
    private String applyContent;

}
