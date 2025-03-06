package com.paysphere.controller.request;

import com.paysphere.config.EnumValid;
import com.paysphere.enums.AccountTypeEnum;
import com.paysphere.enums.CurrencyEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SettleAccountAddReq {

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
     * 账户类型
     */
    @NotNull(message = "accountType is null")
    @EnumValid(target = AccountTypeEnum.class, transferMethod = "getCode", message = "accountType type not support")
    private Integer accountType;

    /**
     * 账户号
     */
    @NotBlank(message = "accountNo is required")
    private String accountNo;

    /**
     * 账户名称
     */
    @NotBlank(message = "accountName is required")
    private String accountName;

    /**
     * 币种
     */
    @NotBlank(message = "currency is required")
    @EnumValid(target = CurrencyEnum.class, message = "currency type not support")
    private String currency;

}
