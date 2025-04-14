package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账户每日余额快照实体
 * 记录账户每日的余额快照，用于对账和统计分析
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "settle_account_snapshot")
public class SettleAccountSnapshot extends BaseSettleEntity {

    // ============== 时间信息 ==============
    /**
     * 账务日期
     * 快照的日期，格式：yyyy-MM-dd
     */
    private LocalDate accountDate;

    // ============== 账户信息 ==============

    /**
     * 账户名称
     * 账户的显示名称
     */
    private String accountName;

    /**
     * 账户类型
     * 1: 商户账户
     * 2: 平台账户
     * 3: 渠道账户
     */
    private Integer accountType;


    // ============== 余额信息 ==============
    /**
     * 币种
     * 账户的币种，如：CNY、USD等
     */
    private String currency;

    /**
     * 可用余额
     * 账户当前可用的余额
     */
    private BigDecimal availableBalance;

    /**
     * 冻结余额
     * 账户中被冻结的余额
     */
    private BigDecimal frozenBalance;

    /**
     * 待结算余额
     * 账户中待结算的余额
     */
    private BigDecimal toSettleBalance;

}
