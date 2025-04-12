package app.sphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class HistoryListDTO {

    /**
     * Transaction identifier on service provider system. Must be filled upon successful transaction
     */
    private String referenceNo;

    /**
     * Transaction identifier on consumer service system
     */
    private String partnerReferenceNo;

    /**
     * data
     */
    private List<HistoryListDetailDataDTO> detailData;

    /**
     * additional information
     */
    private String additionalInfo;
}
