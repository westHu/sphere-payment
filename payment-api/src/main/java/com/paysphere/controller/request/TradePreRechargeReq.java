package com.paysphere.controller.request;

import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class TradePreRechargeReq {

    /**
     * 充值目的
     */
    @Length(max = 32, message = "purpose|remark length max 32")
    private String purpose;

    /**
     * 充值商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 充值商户名称
     */
    @NotBlank(message = "merchantName is required")
    private String merchantName;

    /**
     * 充值账户
     */
    @NotBlank(message = "accountNo is required")
    private String accountNo;

    /**
     * 币种
     */
    @NotBlank(message = "currency is required")
    private String currency;

    /**
     * 充值金额
     */
    @NotNull(message = "amount is required")
    private BigDecimal amount;

    /**
     * 支付方式
     */
    @NotNull(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 金额加个限制
     */
    public BigDecimal getAmount() {
        if (Objects.nonNull(amount) && amount.compareTo(new BigDecimal("10000")) < 0) {
            throw new PaymentException(ExceptionCode.RECHARGE_MIN_AMOUNT_ERROR);
        }
        if (Objects.nonNull(amount) && amount.compareTo(new BigDecimal("1000000000")) > 0) {
            throw new PaymentException(ExceptionCode.RECHARGE_MAX_AMOUNT_ERROR);
        }
        return amount;
    }
}
