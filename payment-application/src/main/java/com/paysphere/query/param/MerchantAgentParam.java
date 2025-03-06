package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantAgentParam {

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 邀请码
     */
    private String invitationCode;
}
