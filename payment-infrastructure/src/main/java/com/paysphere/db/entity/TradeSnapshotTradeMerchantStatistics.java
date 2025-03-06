package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商户维度 交易数据分析快照 (收款、代付)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "snapshot_trade_merchant_statistics")
public class TradeSnapshotTradeMerchantStatistics extends BaseEntity {

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
     * 收款订单笔数
     */
    private Integer payOrderCount;

    /**
     * 收款订单成功笔数
     */
    private Integer payOrderSuccessCount;

    /**
     * 订单金额
     */
    private BigDecimal payOrderAmount;

    /**
     * 收款订单成功金额
     */
    private BigDecimal payOrderSuccessAmount;

    /**
     * 代付订单笔数
     */
    private Integer cashOrderCount;

    /**
     * 代付订单成功笔数
     */
    private Integer cashOrderSuccessCount;

    /**
     * 代付订单金额
     */
    private BigDecimal cashOrderAmount;

    /**
     * 代付订单成功金额
     */
    private BigDecimal cashOrderSuccessAmount;
    
    /**
     * 扩展
     */
    private String attribute;

}
