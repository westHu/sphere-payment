package com.paysphere.command.dto;

import lombok.Data;

@Data
public class TradeChannelErrorDTO {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道编码
     */
    private String channelName;

    /**
     * 渠道错误信息
     */
    private String errorMsg;

}
