package app.sphere.command.cmd;

import share.sphere.enums.TradePaymentSourceEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TradePaymentCmd extends TradeCommand {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 付款方信息
     */
    private PayerCommand payer;

    /**
     * 收款方信息
     */
    private ReceiverCommand receiver;

    /**
     * 有效期
     */
    private Integer expiryPeriod;

    /**
     * 来源
     */
    private TradePaymentSourceEnum tradePaySource;
}
