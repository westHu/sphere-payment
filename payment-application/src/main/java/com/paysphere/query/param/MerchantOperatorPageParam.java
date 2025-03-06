package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantOperatorPageParam extends PageParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 操作员姓名
     */
    private String username;

    /**
     * 角色
     */
    private Integer role;
}
