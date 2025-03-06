package com.paysphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 提现打款订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_withdraw_order")
public class TradeWithdrawOrder extends BaseEntity {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 代付单号
     */
    private String tradeNo;

    /**
     * 交易目的
     */
    private String purpose;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 出款账号
     */
    private String withdrawAccount;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户账户号
     */
    private String accountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 代付金额
     */
    private BigDecimal amount;

    /**
     * 实扣金额
     */
    private BigDecimal actualAmount;

    /**
     * 商户(代理商)分润
     */
    private BigDecimal merchantProfit;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 通道成本金额
     */
    private BigDecimal channelCost;

    /**
     * 平台利润
     */
    private BigDecimal platformProfit;

    /**
     * 付款方信息
     */
    private String payerInfo;

    /**
     * 收款方信息
     */
    private String receiverInfo;

    /**
     * 交易时间
     */
    private Integer tradeTime;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 交易结果
     */
    private String tradeResult;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 支付结果
     */
    private String paymentResult;

    /**
     * 支付完成时间
     */
    private Integer paymentFinishTime;

    /**
     * 结算状态
     */
    private Integer settleStatus;

    /**
     * 结算结果
     */
    private String settleResult;

    /**
     * 结算完成时间
     */
    private Integer settleFinishTime;

    /**
     * 备注
     */
    private String attribute;

}

