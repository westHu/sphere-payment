package app.sphere.query.param;

import lombok.Data;

@Data
public class PaymentChannelMethodRangeParam {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 交易方向
     */
    private Integer paymentDirection;

}
