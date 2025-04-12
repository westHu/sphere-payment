package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商户代付渠道配置
 * 记录商户的代付渠道配置信息，包括渠道信息、费用配置、限额配置等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_payout_channel_config")
public class MerchantPayoutChannelConfig extends BaseEntity {

    // ============== 基础信息 ==============
    /**
     * 商户ID
     * 配置所属商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 配置所属商户的名称
     */
    private String merchantName;

    // ============== 渠道信息 ==============
    /**
     * 支付方式编码
     * 如：银行卡、支付宝、微信等
     */
    private String paymentMethod;

    /**
     * 渠道编码
     * 代付渠道的唯一标识符
     */
    private String channelCode;

    /**
     * 渠道名称
     * 代付渠道的显示名称
     */
    private String channelName;

    /**
     * 优先级
     * 渠道的优先级，数值越小优先级越高
     */
    private Integer priority;

    // ============== 费用配置 ==============
    
    /**
     * 支持的币种
     * 渠道适用的币种
     */
    private String currency;

    /**
     * 费用
     * 单笔固定手续费
     */
    private BigDecimal singleFee;

    /**
     * 费率
     * 交易金额的百分比费率
     */
    private BigDecimal singleRate;

    /**
     * 最少商户手续费
     * 每笔交易的最低手续费
     */
    private BigDecimal minFee;

    // ============== 限额配置 ==============
    /**
     * 单笔最小
     * 单笔交易的最小金额
     */
    private BigDecimal amountLimitMin;

    /**
     * 单笔最大
     * 单笔交易的最大金额
     */
    private BigDecimal amountLimitMax;

    // ============== 结算配置 ==============
    /**
     * 结算配置
     * 结算方式，如：T+0、T+1等
     */
    private String settleType;

    /**
     * 结算时间
     * 具体的结算时间点
     */
    private String settleTime;

    // ============== 状态控制 ==============
    /**
     * 状态
     * true: 启用
     * false: 禁用
     */
    private boolean status;
}
