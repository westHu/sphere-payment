package app.sphere.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradePayOrderPageDTO {

    /**
     * 目的
     */
    private String purpose;

    /**
     * 收款单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String orderNo;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道名称
     */
    private String channelName;

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
     * 手续费
     */
    private BigDecimal merchantFee;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 结算状态
     */
    private Integer settleStatus;

    /**
     * 通知状态
     */
    private Integer callBackStatus;

    /**
     * 交易时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tradeTime;

    /**
     * 支付完成时间
     */
    private Integer paymentFinishTime;

    /**
     * 来源
     */
    private Integer source;

    /**
     * 付款人信息
     */
    private PayerDTO payer;
}
