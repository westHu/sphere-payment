package com.paysphere.mq.dto;


import com.paysphere.enums.SettleTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleCashMessageDTO {

    /**
     * 业务单号-
     */
    private String businessNo;

    /**
     * 交易单号
     */
    @NotBlank(message = "settleCash tradeNo is required")
    private String tradeNo;

    /**
     * 外部订单号
     */
    @NotBlank(message = "settleCash outerNo is required")
    private String outerNo;

    /**
     * 交易时间
     */
    @NotBlank(message = "settleCash tradeTime is required")
    private String tradeTime;

    /**
     * 交易支付完成时间
     */
    @NotBlank(message = "settleCash paymentFinishTime is required")
    private String paymentFinishTime;

    /**
     * 币种
     */
    @NotBlank(message = "settleCash currency is required")
    private String currency;

    /**
     * 交易金额
     */
    @NotNull(message = "settleCash amount is required")
    private BigDecimal amount;

    /**
     * 实际扣款
     */
    @NotNull(message = "settleCash actualAmount is required")
    private BigDecimal actualAmount;

    /**
     * 商户手续费
     */
    @NotNull(message = "settleCash merchantFee is required")
    private BigDecimal merchantFee;

    /**
     * 代理商分润
     */
    @NotNull(message = "settleCash merchantProfit is required")
    private BigDecimal merchantProfit;

    /**
     * 通道成本
     */
    @NotNull(message = "settleCash channelCost is required")
    private BigDecimal channelCost;

    /**
     * 平台利润
     */
    @NotNull(message = "settleCash platformProfit is required")
    private BigDecimal platformProfit;

    /**
     * 到账金额
     */
    @NotNull(message = "settleCash accountAmount is required")
    private BigDecimal accountAmount;

    /**
     * 渠道信息
     */
    @NotBlank(message = "settleCash channelCode is required")
    private String channelCode;

    /**
     * 渠道名称
     */
    @NotBlank(message = "settleCash channelName is required")
    private String channelName;

    /**
     * 支付方式
     */
    @NotBlank(message = "settleCash paymentMethod is required")
    private String paymentMethod;

    /**
     * 支付方式
     */
    @NotBlank(message = "settleCash paymentName is required")
    private String paymentName;

    /**
     * 商户信息
     */
    @NotBlank(message = "settleCash merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "settleCash merchantName is required")
    private String merchantName;

    /**
     * 商户账户号
     */
    @NotBlank(message = "settleCash accountNo is required")
    private String accountNo;

    /**
     * 扣费方式
     */
    @NotNull(message = "settleCash deductionType is required")
    private Integer deductionType;

    /**
     * 结算方式
     */
    private String settleType = SettleTypeEnum.D0.name();

    /**
     * 结算时间
     */
    private String settleTime;
}
