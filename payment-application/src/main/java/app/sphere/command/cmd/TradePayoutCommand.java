package app.sphere.command.cmd;

import lombok.Data;
import share.sphere.enums.TradePayoutSourceEnum;

@Data
public class TradePayoutCommand extends TradeCommand {

    /**
     * 支付方式, 确定为数字编码
     */
    private String paymentMethod;

    /**
     * 出款账号， 可能是银行卡号、可能是钱包账号、可能是其他
     */
    private String bankCode;

    /**
     * 出款账号， 可能是银行卡号、可能是钱包账号、可能是其他
     */
    private String bankAccount;

    /**
     * payer info
     * 付款方信息
     */
    private PayerCommand payer;

    /**
     * receiver info
     * 收款方信息
     */
    private ReceiverCommand receiver;

    /**
     * 来源
     */
    private TradePayoutSourceEnum tradePayoutSourceEnum;

}
