package api.sphere.controller.request;

import lombok.Data;

@Data
public class PaymentMethodReq {

    /**
     * 支付方式
     */
    private String paymentMethod;
}
