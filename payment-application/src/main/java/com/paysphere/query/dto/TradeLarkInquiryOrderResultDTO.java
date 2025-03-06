package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeLarkInquiryOrderResultDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 外部单号
     */
    private String merchantNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 支付完成时间
     */
    private String paymentFinishTime;

    /**
     * 状态
     */
    private String paymentStatus;

    /**
     * 备注
     */
    private String remark;

}
