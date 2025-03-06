package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantAgentUpdateCommand {

    /**
     * 代理商名称
     */
    private String agentId;

    /**
     * 代理商电话
     */
    private String agentPhone;

    /**
     * 代理商邮箱
     */
    private String agentEmail;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 代理商头像
     */
    private String agentAvatar;

    /**
     * 状态
     */
    private Integer status;

}
