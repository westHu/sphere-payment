package com.paysphere.command.dto;

import lombok.Data;

@Data
public class TradeFileDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String outerNo;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

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
     * 收款币种
     */
    private String currency = "";

    /**
     * 收款金额  收款金额 - 商户手续费 = 到账金额
     */
    private String amount = "0";

    /**
     * 商户手续费  商户手续费 = 商户分润 + 渠道成本 + 平台利润
     */
    private String merchantFee = "0";

    /**
     * 到账金额
     */
    private String accountAmount = "0";

    /**
     * 商户(代理商)分润
     */
    private String merchantProfit = "0";

    /**
     * 通道成本金额
     */
    private String channelCost = "0";

    /**
     * 平台利润
     */
    private String platformProfit = "0";

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 支付完成时间
     */
    private String paymentFinishTime;

    /**
     * 地区
     */
    private Integer area;
}
