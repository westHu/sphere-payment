package api.sphere.controller.request;

import lombok.Data;

@Data
public class HistoryDetailReq {

    /**
     * Transaction identifier on consumer service system
     */
    private String originalPartnerReferenceNo;

    /**
     * additional information
     */
    private String additionalInfo;

}
