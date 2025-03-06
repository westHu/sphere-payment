package com.paysphere.query.dto;

import lombok.Data;

@Data
public class TradeLarkInquiryOrderDTO {

    /**
     * 校验返回
     */
    private String challenge;

    /**
     * 事件
     */
    private TradeLarkInquiryDecryptEventDTO event;

}
