package com.paysphere.message.dto;

import lombok.Data;

@Data
public class MessageAdditionalInfoDTO {

    /**
     * 付款人姓名（静态码有使用到）
     */
    private String payerName;

    /**
     * 付款人IDNo（静态码有使用到）
     */
    private String payerIdentity;

    /**
     * 付款人支付方式（静态码有使用到）
     */
    private String payerUseMethod;
}
