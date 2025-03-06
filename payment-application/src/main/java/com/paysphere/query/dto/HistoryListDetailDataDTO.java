package com.paysphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoryListDetailDataDTO {

    /**
     * ISODateTime
     */
    private String dateTime;

    /**
     * Net amount of the transaction.
     */
    private HistoryAmountDTO amount;

    /**
     * Transaction remarks.
     */
    private String remark;

    /**
     * The source of funds used for this transaction.
     */
    private List<HistorySourceOfFundsDTO> sourceOfFunds;

    /**
     * Transaction status.
     * INIT, SUCCESS, CLOSED, CANCELLED
     */
    private String status;

    /**
     * transaction type.
     * PAYMENT, REFUND, TOP_UP, SEND_MONEY, RECEIVE_MONEY
     */
    private String type;

    /**
     * Additional information from detail Data
     */
    private String additionalInfo;
}
