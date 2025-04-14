package infrastructure.sphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 沙箱代付订单实体
 * 用于测试环境的代付订单，包含订单基础信息、支付渠道信息、商户信息、金额信息等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sandbox_trade_payout_order")
public class TradeSandboxPayoutOrder extends BaseTradeEntity {

    // ============== 商品信息 ==============
    /**
     * 商品详情
     * 订单相关的商品或服务信息，JSON格式
     */
    private String productDetail;

    // ================ 支付渠道信息 ================
    /**
     * 支付方式
     * 具体的支付方式，如：BANK_CARD、WALLET等
     */
    private String paymentMethod;

    /**
     * 渠道编码
     * 支付渠道的唯一标识，如：ALIPAY、WECHAT等
     */
    private String channelCode;

    /**
     * 渠道名称
     * 支付渠道的显示名称，如：支付宝、微信支付等
     */
    private String channelName;

    /**
     * 银行联行号
     * 收款银行的联行号，用于银行转账
     */
    private String bankCode;

    /**
     * 出款账号
     * 收款方的账号（可能是银行卡号、钱包账号等）
     */
    private String bankAccount;

    // ================ 商户信息 ================
    /**
     * 商户ID
     * 商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 商户的显示名称
     */
    private String merchantName;

    /**
     * 商户账户号
     * 商户在平台上的账户号
     */
    private String accountNo;

    // ================ 金额信息 ================
    /**
     * 币种
     * 交易使用的货币类型，如：CNY、USD等
     */
    private String currency;

    /**
     * 代付金额
     * 订单的原始金额
     */
    private BigDecimal amount;

    /**
     * 实扣金额
     * 实际扣除的金额（含手续费等）
     */
    private BigDecimal actualAmount;

    /**
     * 商户分润
     * 商户获得的分润金额
     */
    private BigDecimal merchantProfit;

    /**
     * 商户手续费
     * 向商户收取的手续费
     */
    private BigDecimal merchantFee;

    /**
     * 到账金额
     * 实际到账的金额（原始金额-手续费）
     */
    private BigDecimal accountAmount;

    /**
     * 通道成本
     * 支付渠道收取的费用
     */
    private BigDecimal channelCost;

    /**
     * 平台分润
     * 平台获得的分润金额
     */
    private BigDecimal platformProfit;

    // ================ 状态信息 ================
    /**
     * 交易状态
     * 0: 待处理
     * 1: 处理中
     * 2: 处理成功
     * 3: 处理失败
     * 4: 已关闭
     */
    private Integer tradeStatus;

    /**
     * 支付状态
     * 0: 未支付
     * 1: 支付中
     * 2: 支付成功
     * 3: 支付失败
     */
    private Integer paymentStatus;

    /**
     * 结算状态
     * 0: 未结算
     * 1: 结算中
     * 2: 结算成功
     * 3: 结算失败
     */
    private Integer settleStatus;

    /**
     * 回调状态
     * 0: 未回调
     * 1: 回调中
     * 2: 回调成功
     * 3: 回调失败
     */
    private Integer callBackStatus;

    // ================ 交易信息 ================
    /**
     * 付款方信息
     * 付款方的详细信息，JSON格式
     */
    private String payerInfo;

    /**
     * 收款方信息
     * 收款方的详细信息，JSON格式
     */
    private String receiverInfo;

    /**
     * 交易时间
     * 订单创建的时间戳
     */
    private Long tradeTime;

    /**
     * 交易结果
     * 交易的具体结果描述
     */
    private String tradeResult;

    /**
     * 支付完成时间
     * 支付完成的时间戳
     */
    private Long paymentFinishTime;

    /**
     * 支付结果
     * 支付的具体结果描述
     */
    private String paymentResult;

    /**
     * 结算完成时间
     * 结算完成的时间戳
     */
    private Long settleFinishTime;

    /**
     * 结算结果
     * 结算的具体结果描述
     */
    private String settleResult;

    // ================ 其他信息 ================

    /**
     * 地区
     * 订单所属地区，如：CN、US等
     */
    private String region;

    /**
     * 来源
     * 订单的来源渠道，如：API、收银台等
     */
    private Integer source;

    /**
     * 版本
     * 订单的版本号，用于乐观锁
     */
    private Integer version;

    /**
     * 退款单号
     * 关联的退款订单号
     */
    private String refundNo;

    // ================ 扩展属性 ================
    /**
     * 扩展属性
     * 用于存储订单的额外配置信息，JSON格式
     */
    private String attribute;
}

