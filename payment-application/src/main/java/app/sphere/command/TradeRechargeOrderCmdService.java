package app.sphere.command;

import app.sphere.command.cmd.*;
import app.sphere.command.dto.PreRechargeDTO;

public interface TradeRechargeOrderCmdService {

    PreRechargeDTO executePreRecharge(TradePreRechargeCommand command);

    boolean executeRecharge(TradeRechargeCommand command);

    void executeRechargeReview(TradeRechargeReviewCommand rCommand);

    boolean reviewRecharge(String tradeNo);
}
