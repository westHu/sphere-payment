package com.paysphere.controller.request;

import lombok.Data;

import java.time.LocalDateTime;

import static com.paysphere.TradeConstant.DF_0;


@Data
public class HistoryListReq {

    /**
     * Transaction identifier on consumer service system
     */
    private String partnerReferenceNo;

    /**
     * starting time range.
     */
    private String fromDateTime = LocalDateTime.now().plusDays(-90).format(DF_0);

    /**
     * Ending time range.
     */
    private String toDateTime = LocalDateTime.now().format(DF_0);

    /**
     * Maximum number of transactions returned in one pagination.
     */
    private int pageSize = 10;

    /**
     * Current page number.
     */
    private int pageNumber = 1;

    /**
     * additional information
     */
    private String additionalInfo;

}
