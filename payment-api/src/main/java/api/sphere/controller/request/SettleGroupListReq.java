package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SettleGroupListReq {

    /**
     * 交易开始时间
     */
    @NotBlank(message = "tradeStartTime is required")
    private String tradeStartTime;

    /**
     * 交易结束时间
     */
    @NotBlank(message = "tradeEndTime is required")
    private String tradeEndTime;

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

}
