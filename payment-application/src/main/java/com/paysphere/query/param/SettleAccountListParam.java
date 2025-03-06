package com.paysphere.query.param;

import lombok.Data;

import java.util.List;


@Data
public class SettleAccountListParam {

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
     * 地区:角色
     */
    private List<Integer> areaList;

}
