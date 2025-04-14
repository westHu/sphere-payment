package app.sphere.query.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleTimelyStatisticsIndexDTO {

    /**
     * 收款
     */
    private BigDecimal payMerchantFee = BigDecimal.ZERO;
    private BigDecimal payMerchantProfit = BigDecimal.ZERO;
    private BigDecimal payChannelCost = BigDecimal.ZERO;
    private BigDecimal payPlatformProfit = BigDecimal.ZERO;

    /**
     * 代付
     */
    private BigDecimal cashMerchantFee = BigDecimal.ZERO;
    private BigDecimal cashMerchantProfit = BigDecimal.ZERO;
    private BigDecimal cashChannelCost = BigDecimal.ZERO;
    private BigDecimal cashPlatformProfit = BigDecimal.ZERO;

    /**
     * 商户
     */
    /*private Integer merchantCount;
    private Integer merchantCount1Add;
    private Integer merchantCount7Add;*/

    /**
     * 商户资金
     */
    private BigDecimal merchantAmount = BigDecimal.ZERO;
    private BigDecimal merchantAmount1Add = BigDecimal.ZERO;
    private BigDecimal merchantAmount7Add = BigDecimal.ZERO;

    /**
     * 平台资金
     */
    private BigDecimal platformAmount = BigDecimal.ZERO;
    private BigDecimal platformAmount1Add = BigDecimal.ZERO;
    private BigDecimal platformAmount7Add = BigDecimal.ZERO;

}
