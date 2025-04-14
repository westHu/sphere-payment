package api.sphere.controller.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class TradePayVO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户平台订单号
     */
    private String orderNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 商户信息
     */
    private MerchantVO merchant;

    /**
     * 支付时间
     */
    private String transactionTime;

    /**
     * 金额
     */
    private MoneyVO money;

    /**
     * 渠道信息
     */
    private TradePayChannelVO channel;

}
