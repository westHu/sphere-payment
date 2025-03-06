package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class PaymentChannelStatusCommand {

    /**
     * ID
     */
    private Long id;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否一起更新channelMethod
     */
    private Boolean relationMethod;


    /**
     * url
     */
    private String url;

    /**
     * license
     */
    private String license;
}
