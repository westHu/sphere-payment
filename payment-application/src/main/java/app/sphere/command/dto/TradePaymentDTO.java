package app.sphere.command.dto;

import lombok.Data;

@Data
public class TradePaymentDTO {
    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户系统的订单号
     */
    private String orderNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 支付时间（拿到va的时间）
     */
    private String transactionTime;

    /**
     * 商户信息
     */
    private TradeMerchantDTO merchant;

    /**
     * 金额
     */
    private TradeMoneyDTO money;

    /**
     * 渠道信息
     */
    private TradePayChannelDTO channel;

}
