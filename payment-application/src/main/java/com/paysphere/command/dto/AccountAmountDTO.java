package com.paysphere.command.dto;

import com.paysphere.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountAmountDTO {

    /**
     * 币种
     */
    private String currency = CurrencyEnum.IDR.name();

    /**
     * 可用余额
     */
    private BigDecimal availableBalance = BigDecimal.ZERO;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance = BigDecimal.ZERO;

    /**
     * 待结算余额
     */
    private BigDecimal toSettleBalance = BigDecimal.ZERO;

}
