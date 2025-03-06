package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantAgentUpdateStatusCommand {

    /**
     * 代理商名称
     */
    private String agentId;

    /**
     * 状态
     */
    private Integer status;

}
