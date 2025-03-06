package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class MerchantAgentDetailDTO {

    /**
     * 代理商信息
     */
    private MerchantAgentDTO merchantAgent;

    /**
     * 代理商收款费率信息
     */
    private List<MerchantAgentPayPaymentConfigDTO> merchantAgentPayPaymentConfigList;

    /**
     * 代理商代付费率信息
     */
    private List<MerchantAgentCashPaymentConfigDTO> merchantAgentCashPaymentConfigList;


}
