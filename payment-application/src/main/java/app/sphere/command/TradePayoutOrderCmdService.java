package app.sphere.command;


import app.sphere.command.cmd.*;
import app.sphere.command.dto.TradePayoutDTO;

public interface TradePayoutOrderCmdService {

    TradePayoutDTO executePayout(TradePayoutCommand command);

    void executePayoutReview(TradePayoutReviewCommand command);

    boolean executeCashSupplement(TradeCashSupplementCommand command);

    boolean executeCashRefund(TradeCashRefundCommand command);
}
