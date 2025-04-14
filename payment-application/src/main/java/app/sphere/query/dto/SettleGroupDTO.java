package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleGroupDTO {

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
     * 渠道编码
     */
    private String channelName;

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 商户分润
     */
    private BigDecimal merchantProfit;

    /**
     * 渠道成本
     */
    private BigDecimal channelCost;

    /**
     * 平台利润
     */
    private BigDecimal platformProfit;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

}
