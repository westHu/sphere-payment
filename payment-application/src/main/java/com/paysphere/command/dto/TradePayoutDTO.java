package com.paysphere.command.dto;

import lombok.Data;

@Data
public class TradePayoutDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户订单号
     */
    private String outerNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 支付时间
     */
    private String disbursementTime;

    /**
     * 商户信息
     */
    private TradeMerchantDTO merchant;

    /**
     * 渠道信息
     */
    private TradePayoutChannelDTO channel;

    /**
     * 金额
     */
    private TradeMoneyDTO money;
}
