package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收款订单
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sandbox_trade_pay_order")
public class SandboxTradePaymentOrder extends BaseEntity {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 收款单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String orderNo;

    /**
     * 交易目的
     */
    private String purpose;

    /**
     * 商户详情
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
     * 收款金额
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
    private LocalDateTime tradeTime;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 交易结果
     */
    private String tradeResult;

    /**
     * 交易结果-Va
     */
    private String tradeResultVa;

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
     * 回调状态
     */
    private Integer callBackStatus;

    /**
     * 来源
     */
    private Integer source;

    /**
     * 备注
     */
    private String remark;

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

}
