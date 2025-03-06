package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantOperatorListParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 操作员姓名
     */
    private String username;
}
