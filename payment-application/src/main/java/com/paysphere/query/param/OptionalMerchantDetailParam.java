package com.paysphere.query.param;

import com.paysphere.enums.MerchantQueryTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OptionalMerchantDetailParam {

    /**
     * 查询类型
     */
    private List<MerchantQueryTypeEnum> typeList;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 地区
     */
    private Integer area;
}
