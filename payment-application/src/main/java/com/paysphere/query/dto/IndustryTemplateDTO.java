package com.paysphere.query.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IndustryTemplateDTO {

    /**
     * 收款费率
     */
    private List<IndustryTemplatePaymentDTO> payIn = new ArrayList<>();


    /**
     * 代付费率
     */
    private List<IndustryTemplatePaymentDTO> payOut = new ArrayList<>();

}
