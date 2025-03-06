package com.paysphere.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paysphere.enums.AgentStatusEnum;
import com.paysphere.enums.AreaEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantAgentVO {

    /**
     * 代理商父ID
     */
    private String agentParentId;

    /**
     * 代理商ID
     */
    private String agentId;

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
     *
     * @see AgentStatusEnum
     */
    private Integer status;

    /**
     * 地区
     *
     * @see AreaEnum
     */
    private Integer area;

    /**
     * 扩展
     */
    private String attribute;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
