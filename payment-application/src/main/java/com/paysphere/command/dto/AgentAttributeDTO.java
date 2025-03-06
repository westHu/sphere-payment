package com.paysphere.command.dto;

import lombok.Data;

@Data
public class AgentAttributeDTO {

    /**
     * 头像信息
     */
    private String agentAvatar;

    /**
     * 提现账户
     */
    private String withdrawPaymentMethod;

    /**
     * 提现账户
     */
    private String withdrawAccount;

    /**
     * 提现账户名称
     */
    private String withdrawAccountName;

    /**
     * 身份证正面 JPG
     */
    private String imgFrontOfIdCard;

    /**
     * 反面 JPG
     */
    private String imgBackOfIdCard;

}
