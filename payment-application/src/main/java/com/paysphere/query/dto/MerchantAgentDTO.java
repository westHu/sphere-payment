package com.paysphere.query.dto;

import lombok.Data;

@Data
public class MerchantAgentDTO {


    private Long id;

    /**
     * 代理商ID
     */
    private String agentId;

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
     * 邀请码
     */
    private String invitationCode;

    /**
     * 邀请链接
     */
    private String invitationLink;

    /**
     * 状态
     */
    private Integer status;

}
