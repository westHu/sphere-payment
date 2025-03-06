package com.paysphere.command;

import com.paysphere.command.cmd.TradeTransferCommand;
import com.paysphere.command.cmd.TradeTransferReviewCommand;

public interface TradeTransferOrderCmdService {

    boolean executeTransfer(TradeTransferCommand command);

    void executeTransferReview(TradeTransferReviewCommand tCommand);
}
