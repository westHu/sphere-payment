package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 付款完成参数
 */
@Data
public class PaymentFinishCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private Integer paymentStatus;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 完成时间
     */
    private Long transactionTime;

    /**
     * 通道成本
     */
    private BigDecimal channelCost;

    /**
     * 扩展信息
     */
    private String additionalInfo;
}
