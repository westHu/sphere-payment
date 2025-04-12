package app.sphere.query.param;

import lombok.Data;

@Data
public class HistoryListParam {

    /**
     * Transaction identifier on consumer service system
     */
    private String partnerReferenceNo;

    /**
     * starting time range.
     */
    private String fromDateTime;

    /**
     * Ending time range.
     */
    private String toDateTime;

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
