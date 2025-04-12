package app.sphere.command;

import app.sphere.command.cmd.TradeTransferCommand;
import app.sphere.command.cmd.TradeTransferReviewCommand;

public interface TradeTransferOrderCmdService {

    boolean executeTransfer(TradeTransferCommand command);

    void executeTransferReview(TradeTransferReviewCommand tCommand);
}
