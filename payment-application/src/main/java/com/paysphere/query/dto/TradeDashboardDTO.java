package com.paysphere.query.dto;

import lombok.Data;

@Data
public class TradeDashboardDTO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 支付方式名
     */
    private String paymentName;

    /**
     * 统计数量
     */
    private Integer count;

}
