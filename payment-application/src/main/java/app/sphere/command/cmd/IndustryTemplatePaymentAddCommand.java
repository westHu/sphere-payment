package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IndustryTemplatePaymentAddCommand {

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * 模版名称
     */
    private String templateName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 手续费
     */
    private BigDecimal singleFee;

    /**
     * 手续费率
     */
    private BigDecimal singleRate;

    /**
     * 单笔最小
     */
    private BigDecimal amountLimitMin;

    /**
     * 单笔最大
     */
    private BigDecimal amountLimitMax;

    /**
     * 结算配置
     */
    private String settleType;


}
