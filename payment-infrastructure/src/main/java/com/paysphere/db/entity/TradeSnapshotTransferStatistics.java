package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易数据分析快照 (收款、代付)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "snapshot_transfer_statistics")
public class TradeSnapshotTransferStatistics extends BaseEntity {

    /**
     * 交易日期 维度
     */
    private LocalDate tradeDate;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户账户
     */
    private String accountNo;

    /**
     * 商户账户
     */
    private Integer accountType;

    /**
     * 转账方向：转入转出
     */
    private Integer transferDirection;

    /**
     * 订单笔数
     */
    private Integer orderCount;

    /**
     * 订单成功笔数
     */
    private Integer orderSuccessCount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 订单成功金额
     */
    private BigDecimal orderSuccessAmount;

    /**
     * 扩展
     */
    private String attribute;

}
