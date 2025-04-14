package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class SettleAccountRechargeCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    // ================ 金额信息 ================
    /**
     * 充值币种 - 用户充值的原始币种
     */
    private String rechargeCurrency;

    /**
     * 充值金额 - 用户充值的原始金额（以充值币种计算）
     */
    private BigDecimal rechargeAmount;

    /**
     * 兑换率 - 充值币种到入账币种的兑换率
     */
    private BigDecimal exchangeRate;

    /**
     * 入账币种 - 资金入账的币种，可能与充值币种不同
     */
    private String currency;

    /**
     * 入账金额 - 实际入账的金额（以入账币种计算）
     */
    private BigDecimal amount;

    /**
     * 商户分润 - 商户获得的分润金额（以入账币种计算）
     */
    private BigDecimal merchantProfit;

    /**
     * 商户手续费 - 向商户收取的手续费（以入账币种计算）
     */
    private BigDecimal merchantFee;

    /**
     * 到账金额 - 商户实际到账金额（以入账币种计算）
     */
    private BigDecimal accountAmount;

    /**
     * 通道成本 - 支付渠道收取的费用（以入账币种计算）
     */
    private BigDecimal channelCost;

    /**
     * 平台利润 - 平台获得的利润（以入账币种计算）
     */
    private BigDecimal platformProfit;


}
