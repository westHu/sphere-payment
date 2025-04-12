package app.sphere.command.cmd;

import lombok.Data;

@Data
public class TradeWithdrawReviewCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 审核状态
     */
    private boolean reviewStatus;

    /**
     * 审核意见
     */
    private String reviewMsg;
}
