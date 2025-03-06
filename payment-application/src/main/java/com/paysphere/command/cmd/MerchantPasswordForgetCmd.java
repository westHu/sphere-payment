package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantPasswordForgetCmd {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
