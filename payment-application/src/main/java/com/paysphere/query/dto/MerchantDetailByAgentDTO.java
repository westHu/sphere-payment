package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class MerchantDetailByAgentDTO {


    /**
     * 商户基本信息
     */
    private MerchantBaseDTO merchant;

    /**
     * 收款渠道配置
     */
    private List<MerchantPaymentChannelConfigDTO> merchantPayPaymentConfigList;

    /**
     * 代付渠道配置
     */
    private List<MerchantCashPaymentChannelConfigDTO> merchantCashPaymentConfigList;

}
