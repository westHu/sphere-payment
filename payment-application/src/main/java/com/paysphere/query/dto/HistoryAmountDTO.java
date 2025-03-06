package com.paysphere.query.dto;

import lombok.Data;

@Data
public class HistoryAmountDTO {

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private String value;

}
