package app.sphere.command.dto;

import app.sphere.query.dto.TradeCashierStyleDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeCashierPaymentDTO {

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
    private Long expiryPeriod;

    /**
     * 支付方式结果
     */
    private String methodResult;

    /**
     * 支付方式结果类型
     * display:当前页面展示 例如va ,pix, qris
     * redirect:链接，需要跳转
     * 以后还有其他
     */
    private String methodResultOptType;

    /**
     * 样式
     */
    private TradeCashierStyleDTO style;
}
