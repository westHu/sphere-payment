package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class PayerCommand {

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 身份ID
     */
    private String identity;

    /**
     * 付款方式
     */
    private String useMethod;

}
