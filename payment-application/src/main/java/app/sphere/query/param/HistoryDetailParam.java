package app.sphere.query.param;

import lombok.Data;

@Data
public class HistoryDetailParam {

    /**
     * Transaction identifier on consumer service system
     */
    private String originalPartnerReferenceNo;

    /**
     * additional information
     */
    private String additionalInfo;

}
