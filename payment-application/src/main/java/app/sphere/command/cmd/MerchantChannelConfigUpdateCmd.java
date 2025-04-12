package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantChannelConfigUpdateCmd {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * 优先级
     */
    private Integer priority;

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

    /**
     * 结算时间
     */
    private String settleTime;

    /**
     * 商户渠道配置状态
     */
    private Boolean status;
}
