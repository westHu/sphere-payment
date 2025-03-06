package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantOperatorUpdateCmd {

    /**
     * ID
     */
    private Long id;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色
     */
    private Long role;
}
