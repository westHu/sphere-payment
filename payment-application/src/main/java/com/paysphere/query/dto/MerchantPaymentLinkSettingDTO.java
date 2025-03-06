package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class MerchantPaymentLinkSettingDTO {

    /**
     * logo
     */
    private String logo;

    /**
     * bgColor
     */
    private String bgColor;

    /**
     * 推荐支付方式
     */
    private List<String> recommendedPaymentMethod;

    /**
     * 排序支付方式
     */
    private List<PaymentMethodSortedDTO> sortedPaymentMethodList;

}
