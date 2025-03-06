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
@TableName(value = "snapshot_trade_statistics")
public class TradeSnapshotTradeStatistics extends BaseEntity {

    /**
     * 交易日期 维度
     */
    private LocalDate tradeDate;

    /**
     * 交易类型 维度
     */
    private Integer tradeType;

    /**
     * 商户ID 维度
     */
    private String merchantId;

    /**
     * 商户名称 维度
     */
    private String merchantName;

    /**
     * 支付方式 维度
     */
    private String paymentMethod;

    /**
     * 渠道编号 维度
     */
    private String channelCode;

    /**
     * 渠道名称 维度
     */
    private String channelName;

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
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 商户入账金额
     */
    private BigDecimal accountAmount;

    /**
     * 平台通道成本
     */
    private BigDecimal channelCost;

    /**
     * 扩展
     */
    private String attribute;

}
