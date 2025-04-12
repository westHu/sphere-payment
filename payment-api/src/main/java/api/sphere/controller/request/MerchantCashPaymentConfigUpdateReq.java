package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantCashPaymentConfigUpdateReq {

    /**
     * 商户
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 费用
     */
    private BigDecimal singleFee;

}
