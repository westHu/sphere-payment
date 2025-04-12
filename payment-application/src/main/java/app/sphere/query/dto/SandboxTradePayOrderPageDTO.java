package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SandboxTradePayOrderPageDTO {

    /**
     * 收款单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String orderNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

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
    private BigDecimal amount;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 通知状态
     */
    private Integer callBackStatus;

    /**
     * 交易时间
     */
    private Long tradeTime;

    /**
     * 支付完成时间
     */
    private Long paymentFinishTime;


}
