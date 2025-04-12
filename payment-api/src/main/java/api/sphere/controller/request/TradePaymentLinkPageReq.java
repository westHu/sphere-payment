package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradePaymentLinkPageReq extends PageReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 支付链接单号
     */
    private String linkNo;

    /**
     * 支付链接
     */
    private String paymentLink;

    /**
     * 创建开始时间
     */
    @NotBlank(message = "createStartTime is required")
    private String createStartTime;

    /**
     * 创建结束时间
     */
    @NotBlank(message = "createEndTime is required")
    private String createEndTime;

}
