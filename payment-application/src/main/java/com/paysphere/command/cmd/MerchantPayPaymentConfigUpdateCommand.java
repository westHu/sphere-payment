package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantPayPaymentConfigUpdateCommand {

    private String merchantId;

    private BigDecimal singleRate;

    private BigDecimal singleFee;

}
