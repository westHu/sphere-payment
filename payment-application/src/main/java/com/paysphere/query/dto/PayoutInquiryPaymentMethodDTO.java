package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class PayoutInquiryPaymentMethodDTO {

    /**
     * 商户ID
     */
    private MerchantIdDTO merchant;

    /**
     * 附件信息
     */
    private String additionalInfo;

    /**
     * 支付方式
     */
    private List<MerchantPaymentMethodListDTO> paymentMethodList;

}
