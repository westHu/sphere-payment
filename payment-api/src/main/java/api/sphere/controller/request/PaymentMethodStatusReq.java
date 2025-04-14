package api.sphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentMethodStatusReq {

    /**
     * ID
     */
    @NotNull(message = "id is null")
    private Long id;

    /**
     * 支付方式状态
     */
    @NotNull(message = "status is null")
    private Boolean status;

}
