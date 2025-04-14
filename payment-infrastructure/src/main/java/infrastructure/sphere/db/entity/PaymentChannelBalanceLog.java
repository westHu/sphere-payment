package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 渠道余额统计表
 * </p>
 *
 * @author ${author}
 * @since 2023-07-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_channel_balance_log")
public class PaymentChannelBalanceLog extends BaseEntity {

    // ============== 基础信息 ==============
    /**
     * 渠道编码
     * 关联的支付渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     * 关联的支付渠道名称
     */
    private String channelName;

    // ============== 余额信息 ==============
    /**
     * 币种
     * 余额的币种，如：CNY、USD等
     */
    private String currency;

    /**
     * 余额
     * 渠道的总余额
     */
    private BigDecimal balance;

    /**
     * 可用余额
     * 渠道的可用余额，不包括处理中的金额
     */
    private BigDecimal availableBalance;

    /**
     * 处理中金额
     * 渠道正在处理中的交易金额
     */
    private BigDecimal pendingBalance;

    // ============== 备注信息 ==============
    /**
     * 备注
     * 余额变动的备注信息
     */
    private String attribute;

}
