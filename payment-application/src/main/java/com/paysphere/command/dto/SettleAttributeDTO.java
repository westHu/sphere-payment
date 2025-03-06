package com.paysphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class SettleAttributeDTO {

    /**
     * 失败原因
     */
    private String error;

    /**
     * 商户分润明细
     */
    private List<MerchantProfitDTO> merchantProfitList;

}
