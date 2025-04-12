package app.sphere.command.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeWithdrawAmountDTO {

    /**
     * 金额单位
     */
    private String currency;

    /**
     * 提现金额
     */
    private BigDecimal withdrawAmount;

    /**
     * 实扣金额
     */
    private BigDecimal actualAmount;

    /**
     * 实际到账金额
     */
    private BigDecimal accountAmount;

}
