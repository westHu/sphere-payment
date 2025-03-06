package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantAgentSwitchCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 代理商名称
     */
    private String agentId;

    /**
     * 代理商名称
     */
    private String agentName;

}
