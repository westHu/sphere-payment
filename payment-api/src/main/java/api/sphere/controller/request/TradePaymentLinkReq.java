package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import share.sphere.TradeConstant;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.math.BigDecimal;

@Data
public class TradePaymentLinkReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "merchantName is required")
    private String merchantName;

    /**
     * 币种
     */
    @NotBlank(message = "currency is required")
    private String currency;

    /**
     * 金额
     */
    @NotNull(message = "amount is required")
    private BigDecimal amount;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 过期时间（秒）
     */
    private Integer expiryPeriod;

    /**
     * 通知邮箱
     */
    private String notificationEmail;

    /**
     * 备注
     */
    private String notes;

    /**
     * 金额校验
     */
    public BigDecimal getAmount() {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "amount should more than 0");
        }

        if (amount.compareTo(new BigDecimal(TradeConstant.AMOUNT_MAX)) >= 0) {
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "amount should less than " + TradeConstant.AMOUNT_MAX);
        }

        return amount;
    }
}
