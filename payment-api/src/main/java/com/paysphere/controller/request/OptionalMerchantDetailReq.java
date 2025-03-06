package com.paysphere.controller.request;

import com.paysphere.enums.MerchantQueryTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OptionalMerchantDetailReq {

    /**
     * 查询类型
     */
    @NotEmpty(message = "typeList is required")
    private List<MerchantQueryTypeEnum> typeList;

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 交易金额
     */
    private BigDecimal amount;
}
