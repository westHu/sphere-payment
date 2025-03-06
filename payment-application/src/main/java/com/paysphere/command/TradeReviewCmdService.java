package com.paysphere.command;

import com.paysphere.command.cmd.TradeReviewCommand;

public interface TradeReviewCmdService {

    boolean executeTradeReview(TradeReviewCommand command);
}
