package com.paysphere.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paysphere.TradeConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradePayOrderDetailDTO {


    /**
     * 收款单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String outerNo;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 交易目的（备注）
     */
    private String purpose;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

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
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 通道成本
     */
    private BigDecimal channelCost;

    /**
     * 商户入账金额
     */
    private BigDecimal accountAmount;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 回调状态
     */
    private Integer callBackStatus;

    /**
     * 结算状态
     */
    private Integer settleStatus;


    /**
     * 交易时间（开始时间）
     */
    @JsonFormat(pattern = TradeConstant.FORMATTER_0)
    private LocalDateTime tradeTime;

    /**
     * 支付完成时间（结束时间）
     */
    private Integer paymentFinishTime;

    /**
     * 付款人信息
     */
    private PayerDTO payer;

}
