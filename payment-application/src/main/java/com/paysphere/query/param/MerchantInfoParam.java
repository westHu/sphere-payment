package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantInfoParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 操作员名称
     */
    private String username;

}
