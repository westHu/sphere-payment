package infrastructure.sphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 结算订单实体
 * 记录交易订单的结算信息，包括结算金额、手续费、分润等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "settle_order")
public class SettleOrder extends BaseSettleEntity {

    // ============== 订单信息 ==============
    /**
     * 订单单号
     * 系统生成的唯一订单号
     */
    private String tradeNo;

    /**
     * 交易类型
     * 1: 收款
     * 2: 付款
     * 3: 退款
     */
    private Integer tradeType;

    /**
     * 交易时间
     * 订单创建的时间戳
     */
    private Long tradeTime;

    /**
     * 支付完成时间
     * 支付完成的时间戳
     */
    private Long paymentFinishTime;

    // ============== 渠道信息 ==============
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
     * 支付方式
     * 具体的支付方式，如：ALIPAY_APP、WECHAT_APP等
     */
    private String paymentMethod;

    // ============== 结算信息 ==============
    /**
     * 扣费方式
     * 1: 内扣
     * 2: 外扣
     */
    private Integer deductionType;

    /**
     * 币种
     * 交易使用的货币类型，如：CNY、USD等
     */
    private String currency;

    /**
     * 订单金额
     * 订单的原始金额
     */
    private BigDecimal amount;

    /**
     * 结算商户手续费
     * 向商户收取的手续费
     */
    private BigDecimal merchantFee;

    /**
     * 结算商户分润
     * 商户获得的分润金额
     */
    private BigDecimal merchantProfit;

    /**
     * 结算商户通道成本
     * 支付渠道收取的费用
     */
    private BigDecimal channelCost;

    /**
     * 平台盈利
     * 平台获得的分润金额
     */
    private BigDecimal platformProfit;

    /**
     * 到账金额
     * 实际到账的金额（原始金额-手续费）
     */
    private BigDecimal accountAmount;

    // ============== 结算配置 ==============
    /**
     * 结算类型
     * T+0: 实时结算
     * T+1: 次日结算
     * T+N: N天后结算
     */
    private String settleType;

    /**
     * 结算时间
     * 具体的结算时间点，如：09:00
     */
    private String settleTime;

    /**
     * 实际结算时间
     * 实际完成结算的时间戳
     */
    private Long actualSettleTime;

    // ============== 状态控制 ==============
    /**
     * 结算状态
     * 0: 未结算
     * 1: 结算中
     * 2: 结算成功
     * 3: 结算失败
     */
    private Integer settleStatus;

    // ============== 其他信息 ==============
    /**
     * 地区
     * 订单所属地区，如：CN、US等
     */
    private String region;

    // ============== 扩展属性 ==============
    /**
     * 属性
     * 用于存储订单的额外配置信息，JSON格式
     */
    private String attribute;
}
