package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class PaymentMethodStatusCommand {

    /**
     * ID
     */
    private Long id;

    /**
     * 支付方式状态
     */
    private Boolean status;

    /**
     * 是否一起更新channelMethod
     */
    private boolean relationMethod;

}
