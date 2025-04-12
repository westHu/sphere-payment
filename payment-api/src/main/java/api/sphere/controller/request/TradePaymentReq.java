package api.sphere.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TradePaymentReq extends TradeReq {

    /**
     * 支付方式
     */
    @Length(max = 32, message = "paymentMethod length max 32")
    private String paymentMethod;

    /**
     * 付款方信息
     */
    private PayerReq payer;

    /**
     * 收款方信息
     */
    @NotNull(message = "receiver is required")
    @Valid
    private ReceiverReq receiver;

    /**
     * 有效期
     */
    private Integer expiryPeriod;

    /**
     * 支付方式转成大写
     */
    public String getPaymentMethod() {
        if (StringUtils.isNotBlank(paymentMethod)) {
            return paymentMethod.toUpperCase();
        }
        return paymentMethod;
    }
}
