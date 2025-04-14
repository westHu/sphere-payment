package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentChannelMethodFeeRangeDTO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * Fees charged for each transaction
     * 单笔费用
     */
    private BigDecimal singleFee;

    /**
     * Fees charged for each transaction rate
     * 单笔费率
     */
    private BigDecimal singleRate;

    /**
     * Lower amount limit, default:0
     * 单笔金额下限
     */
    private BigDecimal amountLimitMin;

    /**
     * Limit amount ceiling
     * 单笔金额上限
     */
    private BigDecimal amountLimitMax;


}
