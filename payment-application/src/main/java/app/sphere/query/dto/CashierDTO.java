package app.sphere.query.dto;

import lombok.Data;
import share.sphere.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CashierDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private String tradeTime;


    /**
     * 商户名称
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 收单金额
     */
    private String currency = CurrencyEnum.IDR.name();

    /**
     * 收单金额
     */
    private BigDecimal amount;

    /**
     * 推荐支付方式
     */
    private List<CashierPaymentMethodDTO> recommendedMethod;

    /**
     * 支付方式列表
     */
    private List<CashierPaymentTypeDTO> paymentTypeList;

    /**
     * 选中的支付方式
     */
    private CashierPaymentMethodDTO onMethod;

    /**
     * 支付方式结果
     */
    private String methodResult;

    /**
     * 过期时间（秒）
     */
    private Long expiryPeriod;

    /**
     * 样式
     */
    private TradeCashierStyleDTO style;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

}
