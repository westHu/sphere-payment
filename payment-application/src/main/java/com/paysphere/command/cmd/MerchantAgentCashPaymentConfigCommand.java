package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantAgentCashPaymentConfigCommand {

    private Long id;

    /**
     * 费用
     */
    private BigDecimal singleFee;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 状态
     */
    private boolean status;

}
