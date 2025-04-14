package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OptionalMerchantDetailReq {

    /**
     * 查询类型
     */
    private Integer tradeType;

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
    private BigDecimal amount;
}
