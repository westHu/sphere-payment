package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class IndustryTemplateChannelAddCommand {

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * 支付方式ID
     */
    private Long paymentId;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;
}
