package app.sphere.command.cmd;

import lombok.Data;

@Data
public class PaymentChannelMethodStatusCommand {

    /**
     * 支付方向
     */
    private Integer paymentDirection;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道支付方式状态
     */
    private Boolean status;

}
