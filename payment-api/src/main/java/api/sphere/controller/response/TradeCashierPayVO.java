package api.sphere.controller.response;

import app.sphere.query.dto.TradeCashierStyleDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeCashierPayVO {

    /**
     * 订单号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private String tradeTime;

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
     * 支付方式
     */
    private String paymentName;

    /**
     * 币种
     */
    private String currency;

    /**
     * 收单金额
     */
    private BigDecimal amount;

    /**
     * 过期时间（秒）
     */
    private Integer expiryPeriod;

    /**
     * 支付方式结果
     */
    private String methodResult;

    /**
     * 样式
     */
    private TradeCashierStyleDTO style;
}
