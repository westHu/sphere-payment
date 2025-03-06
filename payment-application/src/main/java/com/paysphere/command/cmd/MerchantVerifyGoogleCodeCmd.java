package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantVerifyGoogleCodeCmd {

    /**
     * 用户名
     */
    private String username;

    /**
     * 验证码
     */
    private String authCode;
}
