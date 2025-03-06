package com.paysphere.command;

import com.paysphere.command.cmd.TradeWithdrawCommand;
import com.paysphere.command.cmd.TradeWithdrawReviewCommand;

public interface TradeWithdrawOrderCmdService {

    boolean executeWithdraw(TradeWithdrawCommand command);

    void executeWithdrawReview(TradeWithdrawReviewCommand command);

}
