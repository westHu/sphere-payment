package com.paysphere.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayoutInquiryPaymentMethodReq {

    /**
     * 商户ID
     */
    @NotNull(message = "merchant is required")
    @Valid
    private MerchantIdReq merchant;

    /**
     * 附件信息
     */
    private String additionalInfo;

}
