package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlePayoutCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 交易支付完成时间
     */
    private String paymentFinishTime;

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 实际扣款
     */
    private BigDecimal actualAmount;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 代理商分润
     */
    private BigDecimal merchantProfit;

    /**
     * 通道成本
     */
    private BigDecimal channelCost;

    /**
     * 平台利润
     */
    private BigDecimal platformProfit;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 渠道信息
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 商户信息
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户账户号
     */
    private String accountNo;

    /**
     * 扣费方式
     */
    private Integer deductionType;

    /**
     * 结算方式
     */
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;
}
