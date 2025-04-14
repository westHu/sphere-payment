package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradePlatformDailySnapchatDTO {

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 币种
     */
    private String currency;

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
     * 通道成本
     */
    private BigDecimal channelCost;

    /**
     * 订单成功数量
     */
    private Integer orderSuccessCount;

    /**
     * 成功率
     */
    private BigDecimal successRate;


}
