package com.paysphere.query.dto;

import lombok.Data;

@Data
public class MerchantAccountDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户角色
     */
    private Integer role;

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;


}
