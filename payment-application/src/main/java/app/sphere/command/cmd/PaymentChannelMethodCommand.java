package app.sphere.command.cmd;

import lombok.Data;

@Data
public class PaymentChannelMethodCommand {

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 对接的渠道编码
     * unique no
     */
    private String channelCode;

    /**
     * 对接的渠道名称：duiTku、MidTrans、DANA...
     * unique name
     */
    private String channelName;

}
