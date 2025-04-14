package api.sphere.controller.response;

import app.sphere.query.dto.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class TradeCashierVO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 交易明细
     */
    private String itemDetailInfo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 收单金额
     */
    private String currency;

    /**
     * 收单金额
     */
    private BigDecimal amount;

    /**
     * 推荐支付方式
     */
    private List<CashierPaymentMethodDTO> recommendedMethod = new ArrayList<>();

    /**
     * 支付方式列表
     */
    private List<CashierPaymentTypeDTO> paymentTypeList = new ArrayList<>();

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
    private Integer expiryPeriod;

    /**
     * 样式
     */
    private TradeCashierStyleDTO style;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

}

