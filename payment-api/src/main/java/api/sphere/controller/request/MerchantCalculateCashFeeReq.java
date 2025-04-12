package api.sphere.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantCalculateCashFeeReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 交易金额
     */
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.00", message = "amount should greater zero")
    private BigDecimal amount;
}
