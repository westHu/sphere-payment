package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentChannelMethodStatusReq {

    /**
     * 支付方向
     */
    @NotNull(message = "status is null")
    private Integer paymentDirection;

    /**
     * 渠道编码
     */
    @NotBlank(message = "status is null")
    private String channelCode;

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is null")
    private String paymentMethod;

    /**
     * 渠道支付方式状态
     */
    @NotNull(message = "status is null")
    private Boolean status;

}
