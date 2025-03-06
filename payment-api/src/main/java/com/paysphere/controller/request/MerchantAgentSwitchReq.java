package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantAgentSwitchReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "agentId is required")
    private String merchantId;

    /**
     * 代理商名称
     */
    @NotBlank(message = "agentId is required")
    private String agentId;

    /**
     * 代理商名称
     */
    @NotBlank(message = "agentName is required")
    private String agentName;

}
