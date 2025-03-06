package com.paysphere.command.dto.trade.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantResultDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户编码
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 商户收款完成回调地址
     */
    private String finishPaymentUrl;

    /**
     * 商户出款完成回调地址
     */
    private String finishCashUrl;

    /**
     * 商户跳转地址
     */
    private String finishRedirectUrl;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 费用
     */
    private BigDecimal singleFee;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 实际扣款 (代付使用)
     */
    private BigDecimal actualAmount;

    /**
     * 商户(代理商)分润
     */
    private BigDecimal merchantProfit;

    /**
     * 手续费
     */
    private BigDecimal merchantFee;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 结算配置
     */
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;

}
