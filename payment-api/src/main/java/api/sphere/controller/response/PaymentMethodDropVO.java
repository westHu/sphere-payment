package api.sphere.controller.response;

import lombok.Data;

@Data
public class PaymentMethodDropVO {

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 支付方式名称
     */
    private String paymentName;

    /**
     * 支付方式简称
     */
    private String paymentAbbr;

}
