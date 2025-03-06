package com.paysphere.controller.request;

import com.paysphere.config.EnumValid;
import com.paysphere.enums.CurrencyEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
public class TradeWithdrawReq {

    /**
     * 目的
     */
    @NotBlank(message = "purpose is required")
    @Length(max = 60, message = "purpose|remark length max 60")
    private String purpose;

    /**
     * 商户ID 必传
     */
    @NotBlank(message = "merchantId is required")
    @Length(max = 32, message = "merchantId max 32")
    private String merchantId;

    /**
     * 商户名称
     */
    @Length(max = 64, message = "merchantName max 64")
    private String merchantName;

    /**
     * 商户账号
     */
    @Length(max = 32, message = "accountNo max 32")
    @NotBlank(message = "accountNo is required")
    private String accountNo;

    /**
     * 币种
     *
     * @see CurrencyEnum
     */
    @NotNull(message = "currency is required")
    @EnumValid(target = CurrencyEnum.class, message = "currency is not support")
    private String currency;

    /**
     * 金额
     */
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.00", message = "amount should greater zero")
    private BigDecimal amount;

    /**
     * 申请人
     */
    @NotBlank(message = "applyOperator is required")
    private String applyOperator;

}
