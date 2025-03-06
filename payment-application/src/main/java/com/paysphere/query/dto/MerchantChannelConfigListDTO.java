package com.paysphere.query.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MerchantChannelConfigListDTO {

    /**
     * 收款费率
     */
    private List<MerchantChannelConfigDTO> paymentChannelConfigList = new ArrayList<>();


    /**
     * 代付费率
     */
    private List<MerchantChannelConfigDTO> payoutChannelConfigList = new ArrayList<>();

}
