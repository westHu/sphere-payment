package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class AddressCmd {

    /**
     * 地址
     */
    private String address;

    /**
     * 城市
     */
    private String city;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 电话
     */
    private String phone;

    /**
     * 国家代码
     */
    private String countryCode;

}