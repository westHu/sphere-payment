package com.paysphere.query.param;

import lombok.Data;


@Data
public class SettleAccountParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 地区
     */
    private Integer area;

}
