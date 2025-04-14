package app.sphere.query.param;

import share.sphere.enums.TradeTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantTradeParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 交易类型
     */
    private TradeTypeEnum tradeTypeEnum;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 地区
     */
    private String region;
}
