package infrastructure.sphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 账户资金流水实体
 * 记录账户的资金变动流水，包括商户账户和平台账户的资金变动
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "account_flow")
public class SettleAccountFlow extends BaseEntity {

    // ============== 交易信息 ==============
    /**
     * 关联交易单号
     * 关联的交易订单号
     */
    private String tradeNo;

    // ============== 商户信息 ==============
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

    // ============== 账户信息 ==============
    /**
     * 账户号
     * 发生资金变动的账户号
     */
    private String accountNo;

    // ============== 资金信息 ==============
    /**
     * 账户资金方向
     * 1: 收入
     * -1: 支出
     */
    private Integer accountDirection;

    /**
     * 账户资金方向描述
     * 资金变动的具体描述，如：收款、付款等
     */
    private String accountDirectionDesc;

    /**
     * 币种
     * 资金变动的币种，如：CNY、USD等
     */
    private String currency;

    /**
     * 变动金额
     * 资金变动的具体金额
     */
    private BigDecimal amount;

    // ============== 时间信息 ==============
    /**
     * 流水记录时间
     * 资金变动发生的时间戳
     */
    private Long flowTime;
}
