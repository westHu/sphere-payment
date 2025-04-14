package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 支付渠道方法实体
 * 定义支付渠道支持的支付方式及其配置，包括支付配置、费用配置、限额配置等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "payment_channel_method")
public class PaymentChannelMethod extends BasePaymentEntity {

    // ============== 基础信息 =============
    /**
     * 支付方式编码
     * 支持的支付方式编码，如：ALIPAY_APP、WECHAT_APP等
     */
    private String paymentMethod;

    // ============== 支付配置 ==============
    /**
     * 交易方向
     * 1: 收款
     * 2: 付款
     * 3: 双向
     */
    private Integer paymentDirection;

    /**
     * 支付属性
     * 支付方式的特定属性配置，JSON格式
     */
    private String paymentAttribute;

    /**
     * 描述信息
     * 支付方式的详细描述
     */
    private String description;

    // ============== 结算配置 ==============
    /**
     * 结算周期
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

    // ============== 费用配置 ==============
    /**
     * 单笔固定费用
     * 每笔交易收取的固定费用
     */
    private BigDecimal singleFee;

    /**
     * 单笔费率
     * 每笔交易收取的费率，如：0.006表示0.6%
     */
    private BigDecimal singleRate;

    // ============== 限额配置 ==============
    /**
     * 单笔最小金额
     * 单笔交易的最小金额限制
     */
    private BigDecimal amountLimitMin;

    /**
     * 单笔最大金额
     * 单笔交易的最大金额限制
     */
    private BigDecimal amountLimitMax;

    /**
     * 每日最小交易次数
     * 每日最小交易笔数限制
     */
    private Integer timesLimitMin;

    /**
     * 每日最大交易次数
     * 每日最大交易笔数限制
     */
    private Integer timesLimitMax;

    // ============== 性能指标 ==============
    /**
     * 支付成功率
     * 渠道支付的成功率，用于路由决策
     */
    private BigDecimal successRate = new BigDecimal(1);

    // ============== 状态控制 ==============
    /**
     * 渠道方法状态
     * true: 启用
     * false: 禁用
     */
    private boolean status;

    // ============== 扩展属性 ==============
    /**
     * 扩展属性
     * 用于存储支付方式的额外配置信息，JSON格式
     */
    private String attribute;
}
