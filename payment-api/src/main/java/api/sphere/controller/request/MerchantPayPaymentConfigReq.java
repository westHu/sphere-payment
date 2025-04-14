package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantPayPaymentConfigReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 状态
     */
    private Boolean status = Boolean.TRUE;

    /**
     * 支付方式
     */
    private String paymentMethod;
}
