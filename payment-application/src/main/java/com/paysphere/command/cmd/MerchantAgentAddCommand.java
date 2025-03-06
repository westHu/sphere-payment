package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantAgentAddCommand {

    /**
     * 代理商父ID
     */
    private String agentParentId;

    /**
     * 代理商名称
     */
    private String agentName;

    /**
     * 代理商电话
     */
    private String agentPhone;

    /**
     * 代理商邮箱
     */
    private String agentEmail;

    /**
     * 代理商头像
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
