package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易数据分析快照实体
 * 记录支付系统中的交易数据统计信息，包括收款、代付、充值、转账、提现等各类交易的订单数量、金额、手续费等
 * 用于数据分析和报表展示
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_snapshot_statistics")
public class TradeSnapshotTradeStatistics extends BaseEntity {

    // ================ 统计维度信息 ================
    /**
     * 交易日期 - 统计的日期维度
     */
    private LocalDate tradeDate;

    /**
     * 交易类型 - 统计的交易类型维度
     * 1: 收款
     * 2: 代付
     * 3: 充值
     * 4: 转账
     * 5: 提现
     */
    private Integer tradeType;

    /**
     * 转账方向 - 仅转账交易有效，标识是转入还是转出统计
     * -1: 转出
     * 1: 转入
     */
    private Integer transferDirection;

    /**
     * 商户ID - 统计的商户维度
     */
    private String merchantId;

    /**
     * 商户名称 - 统计的商户名称
     */
    private String merchantName;

    /**
     * 商户账户号 - 统计的商户账户维度
     */
    private String accountNo;

    /**
     * 支付方式 - 统计的支付方式维度
     */
    private String paymentMethod;

    /**
     * 渠道编号 - 统计的渠道维度
     */
    private String channelCode;

    /**
     * 渠道名称 - 统计的渠道名称
     */
    private String channelName;

    // ================ 订单统计信息 ================
    /**
     * 订单笔数 - 统计期间内的订单总笔数
     */
    private Integer orderCount;

    /**
     * 订单成功笔数 - 统计期间内的成功订单笔数
     */
    private Integer orderSuccessCount;

    /**
     * 币种 - 统计的货币类型
     */
    private String currency;

    /**
     * 订单金额 - 统计期间内的订单总金额
     */
    private BigDecimal orderAmount;

    /**
     * 订单成功金额 - 统计期间内的成功订单金额
     */
    private BigDecimal orderSuccessAmount;

    // ================ 费用统计信息 ================
    /**
     * 商户手续费 - 统计期间内的商户手续费总额
     */
    private BigDecimal merchantFee;

    /**
     * 商户入账金额 - 统计期间内的商户实际入账金额
     */
    private BigDecimal accountAmount;

    /**
     * 平台通道成本 - 统计期间内的渠道成本总额
     */
    private BigDecimal channelCost;

    /**
     * 平台利润 - 统计期间内的平台利润总额
     */
    private BigDecimal platformProfit;

    // ================ 其他信息 ================
    /**
     * 扩展字段 - 用于存储额外的统计信息
     */
    private String attribute;
}
