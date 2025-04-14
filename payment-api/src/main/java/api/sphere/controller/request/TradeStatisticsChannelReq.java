package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TradeStatisticsChannelReq extends PageReq {

    /**
     * 交易开始日期
     */
    @NotBlank(message = "tradeStartDate is required")
    private String tradeStartDate;

    /**
     * 交易结束日期
     */
    @NotBlank(message = "tradeEndDate is required")
    private String tradeEndDate;

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道编码
     */
    private String channelCode;


}
