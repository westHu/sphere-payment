package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class ReceiverCommand {

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 身份ID
     */
    private String identity;

}
