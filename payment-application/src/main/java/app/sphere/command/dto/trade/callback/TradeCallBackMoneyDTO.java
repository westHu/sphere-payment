package app.sphere.command.dto.trade.callback;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 金额
 */
@Data
public class TradeCallBackMoneyDTO {


    /**
     * 币种
     */
    private String currency;

    /**
     * 商户号
     */
    private BigDecimal amount;


}
