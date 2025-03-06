package com.paysphere.query.dto;

import lombok.Data;

@Data
public class MerchantUsingDTO extends MerchantIdDTO {

    /**
     * 是否使用
     */
    private Boolean using;
}
