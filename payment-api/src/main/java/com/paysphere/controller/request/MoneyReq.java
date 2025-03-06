package com.paysphere.controller.request;

import com.paysphere.config.EnumValid;
import com.paysphere.enums.CurrencyEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoneyReq {

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

}
