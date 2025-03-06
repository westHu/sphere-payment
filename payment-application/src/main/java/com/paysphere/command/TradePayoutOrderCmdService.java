package com.paysphere.command;



import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.cmd.TradeCashRefundCommand;
import com.paysphere.command.cmd.TradeCashReviewCommand;
import com.paysphere.command.cmd.TradeCashSupplementCommand;
import com.paysphere.command.dto.TradePayoutDTO;

public interface TradePayoutOrderCmdService {

    TradePayoutDTO executeCash(TradeCashCommand command);

    void executeCashReview(TradeCashReviewCommand command);

    boolean executeCashSupplement(TradeCashSupplementCommand command);

    boolean executeCashRefund(TradeCashRefundCommand command);
}
