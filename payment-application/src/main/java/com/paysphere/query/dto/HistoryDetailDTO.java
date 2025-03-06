package com.paysphere.query.dto;

import lombok.Data;

@Data
public class HistoryDetailDTO {

    private String referenceNo;

    private String partnerReferenceNo;

    private HistoryAmountDTO amount;

    private String cancelledTime;

    private String dateTime;

    private HistoryAmountDTO refundAmount;

    private String remark;

    private HistorySourceOfFundsDTO sourceOfFunds;

    private String status;

    private String type;

    private String additionalInfo;
}
