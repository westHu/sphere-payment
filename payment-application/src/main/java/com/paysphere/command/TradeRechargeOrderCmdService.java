package com.paysphere.command;

import com.paysphere.command.cmd.TradePreRechargeCommand;
import com.paysphere.command.cmd.TradeRechargeCommand;
import com.paysphere.command.cmd.TradeRechargeReviewCommand;
import com.paysphere.command.dto.PreRechargeDTO;

public interface TradeRechargeOrderCmdService {

    PreRechargeDTO executePreRecharge(TradePreRechargeCommand command);

    boolean executeRecharge(TradeRechargeCommand command);

    void executeRechargeReview(TradeRechargeReviewCommand rCommand);

    boolean reviewRecharge(String tradeNo);
}
