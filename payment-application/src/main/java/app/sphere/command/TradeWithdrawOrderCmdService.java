package app.sphere.command;

import app.sphere.command.cmd.TradeWithdrawCommand;
import app.sphere.command.cmd.TradeWithdrawReviewCommand;

public interface TradeWithdrawOrderCmdService {

    boolean executeWithdraw(TradeWithdrawCommand command);

    void executeWithdrawReview(TradeWithdrawReviewCommand command);

}
