package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class TradeReviewCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 审核状态
     */
    private Boolean reviewStatus;

    /**
     * 审核意见
     */
    private String reviewMsg;
}
