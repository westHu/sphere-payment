package api.sphere.controller.response;

import lombok.Data;

@Data
public class TradePayoutVO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 支付时间(支付时间)
     */
    private String disbursementTime;

    /**
     * 商户信息
     */
    private MerchantVO merchant;

    /**
     * 渠道信息
     */
    private TradeCashChannelVO channel;

    /**
     * 金额
     */
    private MoneyVO money;

}
