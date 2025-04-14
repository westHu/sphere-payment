package api.sphere.controller.request;

import api.sphere.config.EnumValid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import share.sphere.enums.SettleTypeEnum;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantChannelConfigUpdateReq extends MerchantIdReq {

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 交易类型
     */
    @NotNull(message = "tradeType is required")
    private Integer tradeType;

    /**
     * 优先级
     */
    private Integer priority = 0;

    /**
     * 手续费
     */
    @DecimalMin(value = "0.00", message = "singleFee should greater 0.00")
    private BigDecimal singleFee;

    /**
     * 手续费率
     */
    @DecimalMin(value = "0.00", message = "singleRate should greater 0.00")
    private BigDecimal singleRate;

    /**
     * 单笔最小
     */
    @DecimalMin(value = "0.00", message = "amountLimitMin should greater 0.00")
    private BigDecimal amountLimitMin;

    /**
     * 单笔最大
     */
    @DecimalMin(value = "0.00", message = "amountLimitMax should greater 0.00")
    private BigDecimal amountLimitMax;

    /**
     * 结算配置
     */
    @EnumValid(target = SettleTypeEnum.class, transferMethod = "name", message = "settleType not support")
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
