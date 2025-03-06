package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_recharge_order")
public class TradeRechargeOrder extends BaseEntity {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 充值单号
     */
    private String tradeNo;

    /**
     * 充值目的
     */
    private String purpose;

    /**
     * 充值商户ID
     */
    private String merchantId;

    /**
     * 充值商户名称
     */
    private String merchantName;

    /**
     * 充值账户
     */
    private String accountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 转账金额
     */
    private BigDecimal amount;

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
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 充值账户
     */
    private String bankAccount;

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
