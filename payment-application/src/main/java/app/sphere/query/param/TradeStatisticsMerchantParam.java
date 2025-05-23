package app.sphere.query.param;

import lombok.Data;

@Data
public class TradeStatisticsMerchantParam extends PageParam {

    /**
     * 交易开始日期
     */
    private String tradeStartDate;

    /**
     * 交易结束日期
     */
    private String tradeEndDate;

    /**
     * 交易类型
     */
    private Integer tradeType;

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

}
