package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 转账订单实体
 * 记录支付系统中的转账订单信息，包括转账金额、手续费、交易状态等
 * 注：一笔转账会产生转出和转入两条记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_transfer_order")
public class TradeTransferOrder extends BaseEntity {

    // ================ 订单基础信息 ================

    /**
     * 转账单号 - 系统生成的唯一订单号
     */
    private String tradeNo;

    /**
     * 转账目的 - 订单的交易用途说明
     */
    private String purpose;

    /**
     * 转账方向 - 标识是转出还是转入记录
     * -1: 转出记录
     * 1: 转入记录
     */
    private Integer direction;

    // ================ 商户信息 ================
    /**
     * 转账商户ID - 转账相关的商户ID
     */
    private String merchantId;

    /**
     * 转账商户名称 - 转账相关的商户名称
     */
    private String merchantName;

    /**
     * 转账账户 - 转账相关的账户号
     */
    private String accountNo;

    // ================ 金额信息 ================
    /**
     * 币种 - 转账使用的货币类型
     */
    private String currency;

    /**
     * 转账金额 - 转账的原始金额
     */
    private BigDecimal amount;

    /**
     * 商户(代理商)分润 - 商户获得的分润金额
     */
    private BigDecimal merchantProfit;

    /**
     * 商户手续费 - 向商户收取的手续费
     */
    private BigDecimal merchantFee;

    /**
     * 到账金额 - 实际到账的金额
     */
    private BigDecimal accountAmount;

    /**
     * 通道成本金额 - 支付渠道收取的费用
     */
    private BigDecimal channelCost;

    /**
     * 平台利润 - 平台获得的利润
     */
    private BigDecimal platformProfit;

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
     * IP地址 - 操作来源的IP地址
     */
    private String ip;

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
