package com.paysphere.controller.response;

import lombok.Data;

@Data
public class MerchantRegisterVO {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户登录用户名
     */
    private String username;

    /**
     * 商户登录密码
     */
    private String password;

    /**
     * 商户登录密码
     */
    private String tradePassword;
}
