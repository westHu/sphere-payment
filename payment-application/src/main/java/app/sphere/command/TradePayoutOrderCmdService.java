package app.sphere.command;


import app.sphere.command.cmd.TradePayoutReviewCommand;
import app.sphere.command.dto.TradePayoutDTO;
import app.sphere.command.cmd.TradeCashRefundCommand;
import app.sphere.command.cmd.TradeCashSupplementCommand;
import app.sphere.command.cmd.TradePayoutCommand;

public interface TradePayoutOrderCmdService {

    TradePayoutDTO executePayout(TradePayoutCommand command);

    void executePayoutReview(TradePayoutReviewCommand command);

    boolean executeCashSupplement(TradeCashSupplementCommand command);

    boolean executeCashRefund(TradeCashRefundCommand command);
}
