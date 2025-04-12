package api.sphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentMethodUpdateReq {

    /**
     * ID
     */
    @NotNull(message = "id is null")
    private Long id;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 支付方式方向
     */
    private Integer paymentDirection;

    /**
     * 支付方式类型
     */
    private Integer paymentType;

    /**
     * 支付方式图标
     */
    private String paymentIcon;

}
