package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeMerchantStatisticsSnapshotDTO {

    /**
     * 交易日期
     */
    private String tradeDate;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 收款金额
     */
    private BigDecimal payAmount;

    /**
     * 收款成功金额
     */
    private BigDecimal paySuccessAmount;

    /**
     * 收款成功率
     */
    private BigDecimal paySuccessRate;

    /**
     * 代付金额
     */
    private BigDecimal cashAmount;

    /**
     * 代付成功金额
     */
    private BigDecimal cashSuccessAmount;

    /**
     * 代付成功率
     */
    private BigDecimal cashSuccessRate;

}
