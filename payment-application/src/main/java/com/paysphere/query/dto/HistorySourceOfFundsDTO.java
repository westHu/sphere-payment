package com.paysphere.query.dto;

import lombok.Data;

@Data
public class HistorySourceOfFundsDTO {

    private String source;

    private HistoryAmountDTO amount;

}
