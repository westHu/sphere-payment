package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 充值订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_recharge_order")
public class TradeRechargeOrder extends BaseEntity {

    // ================ 订单基础信息 ================
    /**
     * 充值单号 - 系统生成的唯一订单号
     */
    private String tradeNo;

    /**
     * 充值目的 - 订单的充值用途说明
     */
    private String purpose;

    // ================ 商户信息 ================
    /**
     * 充值商户ID - 商户的唯一标识
     */
    private String merchantId;

    /**
     * 充值商户名称 - 商户的显示名称
     */
    private String merchantName;

    /**
     * 充值账户 - 商户在平台上的账户号
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

    // ================ 支付信息 ================
    /**
     * 支付方式 - 具体的支付方式（如：银行卡、钱包等）
     */
    private String paymentMethod;

     /**
     * 网络 - 数字货币 收款出款都用
     */
    private String network;

    /**
     * 银行联行号
     * 收款银行的联行号
     */
    private String bankCode;

    /**
     * 充值账户 - 用于充值的银行账户或其他账户
     */
    private String bankAccount;

    /**
     * 充值账号名称
     * 充值使用的账户名称
     */
    private String bankAccountName;

    // ================ 交易状态信息 ================
    /**
     * 交易时间 - 订单创建的时间戳
     */
    private Long tradeTime;

    /**
     * 交易状态 - 订单的整体状态
     */
    private Integer tradeStatus;

    /**
     * 交易结果 - 交易处理的结果信息
     */
    private String tradeResult;

    // ================ 结算信息 ================
    /**
     * 结算状态 - 资金结算的状态
     */
    private Integer settleStatus;

    /**
     * 结算结果 - 结算处理的结果信息
     */
    private String settleResult;

    /**
     * 结算完成时间 - 结算完成的时间戳
     */
    private Long settleFinishTime;

    // ================ 其他信息 ================

    /**
     * 来源 - 订单的来源标识
     */
    private Integer source;

    /**
     * 区域 - 订单所属的区域
     */
    private String region;

    /**
     * 版本 - 数据版本号
     */
    private Integer version;

    /**
     * 备注 - 订单的附加说明信息
     */
    private String attribute;
}
