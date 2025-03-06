package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class PaymentChannelMethodStatusCommand {

    /**
     * ID
     */
    private Long id;

    /**
     * 渠道支付方式状态
     */
    private Boolean status;

}
