package com.paysphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户资金流水（包括商户账户、平台账户）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "account_flow")
public class SettleAccountFlow extends BaseEntity {

    /**
     * 商户流水号
     */
    private String accountFlowNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户资金方向 1 收入 -1 支出
     */
    private Integer accountDirection;

    /**
     * 账户资金方向描述
     */
    private String accountDirectionDesc;

    /**
     * 币种
     */
    private String currency;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 关联交易单号
     */
    private String tradeNo;

    /**
     * 关联外部单号
     */
    private String outerNo;

    /**
     * 流水记录时间
     */
    private LocalDateTime flowTime;

    /**
     * 扩展
     */
    private String attribute;

}
