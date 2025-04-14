package app.sphere.command;

import app.sphere.command.cmd.TradeReviewCommand;

public interface TradeReviewCmdService {

    boolean executeTradeReview(TradeReviewCommand command);
}
