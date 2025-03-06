package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantLoginCmd {

    /**
     * 登录类型
     */
    private String mode;

    /**
     * 用户名. 唯一
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
