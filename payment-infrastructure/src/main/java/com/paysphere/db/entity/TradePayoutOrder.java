package com.paysphere.db.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 代付订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_cash_order")
public class TradePayoutOrder extends BaseEntity {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 代付单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String outerNo;

    /**
     * 交易目的
     */
    private String purpose;

    /**
     * 商品详情
     */
    private String productDetail;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道编码
     */
    private String channelName;

    /**
     * 出款账号， 可能是银行卡号、可能是钱包账号、可能是其他
     */
    private String cashAccount;

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
     * 回调状态
     */
    private Integer callBackStatus;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 来源
     */
    private Integer source;

    /**
     * ip
     */
    private String ip;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 地区
     */
    private Integer area;

    /**
     * 备注
     */
    private String attribute;
}

