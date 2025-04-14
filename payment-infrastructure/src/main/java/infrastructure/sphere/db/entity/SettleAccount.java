package infrastructure.sphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商户账户余额
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "settle_account")
public class SettleAccount extends BaseEntity {

    // ============== 基本信息 ==============
    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;

    // ============== 余额信息 ==============
    /**
     * 币种
     */
    private String currency;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;

    /**
     * 待结算余额
     */
    private BigDecimal toSettleBalance;

    // ============== 配置信息 ==============
    /**
     * 状态
     */
    private boolean status;

    /**
     * 地区
     */
    private String region;

    // ============== 系统信息 ==============
    /**
     * 版本
     */
    private Integer version;

    /**
     * 属性
     */
    private String attribute;
}
