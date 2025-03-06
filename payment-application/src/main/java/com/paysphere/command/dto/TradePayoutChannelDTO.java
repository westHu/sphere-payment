package com.paysphere.command.dto;

import lombok.Data;

@Data
public class TradePayoutChannelDTO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * detailed name
     * 名称
     */
    private String paymentName;

    /**
     * 渠道名称
     */
    private String channelCode;

    /**
     * channel
     * 对接的渠道名称
     */
    private String channelName;

    /**
     * 出款账号
     */
    private String cashAccount;

    /**
     * 银行账户名
     */
    private String accountName;

}
