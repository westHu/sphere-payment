package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleAccountAmountFrozenReq {

    /**
     * 业务单号
     */
    @NotBlank(message = "businessNo is required")
    private String businessNo;

    /**
     * 交易单号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 外部订单号
     */
    private String outerNo;

    /**
     * 币种
     */
    @NotBlank(message = "currency is required")
    private String currency;

    /**
     * 冻结金额
     */
    @NotNull(message = "amount is null")
    private BigDecimal amount;

    /**
     * 商户信息
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "merchantName is required")
    private String merchantName;

    /**
     * 商户账户号
     */
    @NotBlank(message = "accountNo is required")
    private String accountNo;

}
