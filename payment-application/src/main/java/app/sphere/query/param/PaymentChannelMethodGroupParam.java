package app.sphere.query.param;

import lombok.Data;

@Data
public class PaymentChannelMethodGroupParam {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 支付方向
     */
    private Integer paymentDirection;

}
