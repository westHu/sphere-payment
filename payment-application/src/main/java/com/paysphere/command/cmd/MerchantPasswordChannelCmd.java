package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantPasswordChannelCmd {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String oldPassword;

    /**
     * 密码
     */
    private String newPassword;
}
