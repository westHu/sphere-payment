package com.paysphere.query.param;


import lombok.Data;

@Data
public class MerchantAccountListParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户类型
     */
    private Integer accountType;

}
