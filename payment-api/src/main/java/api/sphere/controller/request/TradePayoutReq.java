package api.sphere.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
public class TradePayoutReq extends TradeReq {

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 出款账号， 可能是银行卡号、可能是钱包账号、可能是其他
     */
    private String bankCode;

    /**
     * 出款账号， 可能是银行卡号、可能是钱包账号、可能是其他
     */
    @NotBlank(message = "cashAccount is required")
    @Length(max = 32, message = "cashAccount length max 32")
    private String bankAccount;

    /**
     * 付款方信息
     */
    @NotNull(message = "payer is required")
    @Valid
    private PayerReq payer;

    /**
     * 收款方信息
     */
    private ReceiverReq receiver;

    public String getPaymentMethod() {
        if (StringUtils.isNotBlank(paymentMethod)) {
            return paymentMethod.toUpperCase();
        }
        return paymentMethod;
    }
}
