package com.paysphere.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentChannelMethodStatusReq {

    /**
     * ID
     */
    @NotNull(message = "id is null")
    private Long id;

    /**
     * 渠道支付方式状态
     */
    @NotNull(message = "status is null")
    private Boolean status;

}
